package com.example.fintech001.service;

import com.example.fintech001.domain.Member;
import com.example.fintech001.domain.OpenBankToken;
import com.example.fintech001.dto.OpenBankTokenDto;
import com.example.fintech001.dto.TokenRequestDto;
import com.example.fintech001.dto.openbank.OpenBankResponseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fintech001.repository.MemberRepository;
import com.example.fintech001.repository.TokenRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final OpenBankService openBankService;

    @Transactional
    public void saveOpenBankUserToken(TokenRequestDto tokenRequestDto){

        Member member = memberRepository.findById(tokenRequestDto.getMemberId()).orElseThrow();

        if (tokenRepository.existsOpenBankTokenByMemberId(tokenRequestDto.getMemberId())){
            throw new RuntimeException("이미 토큰이 존재함");
        }

       OpenBankResponseToken openBankResponseToken = openBankService.requestToken(tokenRequestDto);

        OpenBankToken openBankToken = OpenBankToken.builder()
                .accessToken(openBankResponseToken.getAccess_token())
                .refreshToken(openBankResponseToken.getRefresh_token())
                .expiresIn((long) openBankResponseToken.getExpires_in())
                .openBankId(openBankResponseToken.getUser_seq_no())
                .memberId(tokenRequestDto.getMemberId())
                .build();

        tokenRepository.save(openBankToken);

        if (!member.hasOpenBankId()) {
            member.updateOpenBankId(openBankResponseToken.getUser_seq_no());
        }
    }

    public OpenBankTokenDto findOpenBankTokenByMemberId(Long memberId){
        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(memberId).orElseThrow();
        return OpenBankTokenDto.of(openBankToken);
    }
}