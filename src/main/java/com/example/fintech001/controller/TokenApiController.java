package com.example.fintech001.controller;

import com.example.fintech001.service.TokenService;
import com.example.fintech001.dto.OpenBankTokenDto;
import com.example.fintech001.dto.TokenRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



    @RestController
    @Slf4j
    @RequiredArgsConstructor
    @CrossOrigin
    @RequestMapping("api")
    public class TokenApiController {
        private final TokenService tokenService;


        @PostMapping("members/openbank/token")
        public ResponseEntity saveOpenBankToken(@RequestBody TokenRequestDto tokenRequestDto){
            tokenService.saveOpenBankUserToken(tokenRequestDto);
            return ResponseEntity.ok().build();
        }

        @GetMapping("members/{id}/openbank/token")
        public ResponseEntity<OpenBankTokenDto> findOpenBankToken(@PathVariable Long id){
            OpenBankTokenDto token = tokenService.findOpenBankTokenByMemberId(id);
            return ResponseEntity.ok().body(token);
        }

    }

