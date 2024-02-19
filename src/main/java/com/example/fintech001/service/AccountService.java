package com.example.fintech001.service;

import com.example.fintech001.domain.Account;
import com.example.fintech001.domain.OpenBankToken;
import com.example.fintech001.dto.AccountDto;
import com.example.fintech001.dto.AccountRequestDto;
import com.example.fintech001.dto.BalanceRequestDto;
import com.example.fintech001.dto.openbank.OpenBankAccountSearchResponseDto;
import com.example.fintech001.dto.openbank.OpenBankBalanceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fintech001.repository.AccountRepository;
import com.example.fintech001.repository.MemberRepository;
import com.example.fintech001.repository.TokenRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final OpenBankService openBankService;


    @Transactional
    public Long saveAccounts(Long memberId) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new NoSuchElementException("없는 유저 입니다.");
        }

        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(memberId).orElseThrow();

        OpenBankAccountSearchResponseDto openBankAccounts = openBankService.findAccount(AccountRequestDto.builder()
                .accessToken(openBankToken.getAccessToken())
                .openBankId(openBankToken.getOpenBankId())
                .build()
        );

        List<Account> accounts = accountRepository.findAccountsByMemberId(memberId);

        HashMap<String, String> fintechUseNumMap = getFintechUseNumMap(accounts);

        List<Account> accountList = openBankAccounts.getRes_list()
                .parallelStream()
                .filter(openBankAccountDto -> !hasFintechUseNum(fintechUseNumMap, openBankAccountDto.getFintech_use_num()))
                .map(
                        resAccount -> Account.builder()
                                .accountNum(resAccount.getAccount_num_masked())
                                .accountSeq(resAccount.getAccount_seq())
                                .bankCode(resAccount.getBank_code_std())
                                .bankName(resAccount.getBank_name())
                                .fintechUseNum(resAccount.getFintech_use_num())
                                .memberId(memberId)
                                .build()
                ).collect(Collectors.toList());

        if (accountList.isEmpty()) {
            return 0L;
        }

        accountRepository.saveAll(accountList);
        return Long.valueOf(accountList.size());
    }


    private HashMap<String, String> getFintechUseNumMap(List<Account> accounts) {
        if (accounts.size() == 0) {
            return new HashMap<>();
        }
        return accounts.parallelStream()
                .collect(Collectors.toMap(
                        account -> account.getFintechUseNum(),
                        account -> account.getFintechUseNum(),
                        (key, value) -> value,
                        HashMap::new
                ));
    }

    private boolean hasFintechUseNum(HashMap<String, String> db, String fintechUseNum) {
        return db.containsKey(fintechUseNum);
    }


    public List<AccountDto> findAccountsByMemberId(Long memberId) {

        if (!memberRepository.existsMemberById(memberId)) {
            throw new NoSuchElementException("없는 유저 입니다.");
        }

        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(memberId).orElseThrow();

        List<Account> accounts = accountRepository.findAccountsByMemberId(memberId);
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("계좌 없음");
        }

        List<AccountDto> accountsWithBalance = accounts.stream()
                .map(account -> CompletableFuture.supplyAsync(() -> {
                            String amt = getBalance(account.getFintechUseNum(), openBankToken.getAccessToken(), memberId);

                            AccountDto accountDto = AccountDto.builder().id(account.getId())
                                    .balanceAmt(amt)
                                    .accountNum(account.getAccountNum())
                                    .accountSeq(account.getAccountSeq())
                                    .bankCode(account.getBankCode())
                                    .fintechUseNum(account.getFintechUseNum())
                                    .bankName(account.getBankName())
                                    .memberId(memberId)
                                    .build();
                            return accountDto;
                        }, getAppropriateThreadPool(accounts.size()))
                )
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparing(AccountDto::getId).reversed())
                .collect(Collectors.toList());
        return accountsWithBalance;
    }

    private ExecutorService getAppropriateThreadPool(int size) {
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(size, 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        return executorService;
    }

    private String getBalance(String fintechUseNum, String openBankAccessToken, Long memberId) {
        String amt = "";
        try {
            OpenBankBalanceResponseDto balance = openBankService
                    .findBalance(BalanceRequestDto
                            .builder()
                            .fintechUseNum(fintechUseNum).accessToken(openBankAccessToken).memberId(memberId).build());
            return balance.getBalance_amt();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return amt;
    }
}