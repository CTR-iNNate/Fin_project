package com.example.fintech001.controller;

import com.example.fintech001.service.MemberService;
import com.example.fintech001.dto.MemberDto;
import com.example.fintech001.dto.SignInDto;
import com.example.fintech001.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("api")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("members/{id}")
    public ResponseEntity<MemberDto> findByMemberId(@PathVariable Long id){
        MemberDto member = memberService.findMemberById(id);
        return ResponseEntity.ok().body(member);
    }

    @PostMapping("members")
    public ResponseEntity<Long> signUp(@RequestBody SignUpDto signUpDto){
        Long memberId = memberService.signUp(signUpDto);
        return ResponseEntity.ok().body(memberId);
    }

    @PostMapping("members/login")
    public ResponseEntity<MemberDto> signIn(@RequestBody SignInDto signInDto){
        MemberDto member = memberService.signIn(signInDto);
        return ResponseEntity.ok().body(member);
    }

    @PutMapping("members/{id}/openbank/user")
    public ResponseEntity add(@PathVariable Long id){
        memberService.addOpenBankInfo(id);
        return ResponseEntity.ok().build();
    }

}