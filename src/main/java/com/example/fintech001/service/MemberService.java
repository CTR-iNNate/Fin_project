package com.example.fintech001.service;

import domain.Member;
import domain.OpenBankToken;
import dto.MemberDto;
import dto.SignInDto;
import dto.SignUpDto;
import dto.openbank.OpenBankAccountDto;
import dto.openbank.OpenBankUserInfoRequestDto;
import dto.openbank.OpenBankUserInfoResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.MemberRepository;
import repository.TokenRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final OpenBankService openBankService;

    @Transactional
    public Long signUp(SignUpDto signUpDto){
        Member member = memberRepository.save(signUpDto.toEntity());
        return member.getId();
    }
    @Transactional
    public void addOpenBankInfo(Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow();

        if (member.hasOpenBankCi()){
            throw new RuntimeException("ci 정보를 이미 가지고있습니다.");
        }

        if (!member.hasOpenBankId()){
            throw new RuntimeException("오픈뱅킹 id를 가지고있지 않습니다.");
        }

        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(memberId).orElseThrow();

        OpenBankUserInfoResponseDto openBankUserInfo = openBankService.findOpenBankUserInfo(OpenBankUserInfoRequestDto
                .builder()
                .accessToken(openBankToken.getAccessToken())
                .openBankId(member.getOpenBankId())
                .build());

        member.updateOpenBankCi(openBankUserInfo.getUser_ci());
    }


    public MemberDto findMemberById(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow();
        return MemberDto.of(member);
    }

    public MemberDto signIn(SignInDto signInDto){
        Member member = memberRepository.findByEmail(signInDto.getEmail()).orElseThrow();

        if (!member.isValidPassword(signInDto.getPassword())){
            throw new IllegalArgumentException("로그인실패");
        }

        return MemberDto.of(member);
    }

}