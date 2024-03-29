package com.example.fintech001.controller;

import com.example.fintech001.service.AccountService;
import com.example.fintech001.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("api")
public class AccountApiController {
    private final AccountService accountService;

    @PostMapping("members/{id}/account")
    public ResponseEntity<Long> saveAccounts(@PathVariable Long id){
        Long size = accountService.saveAccounts(id);
        return ResponseEntity.ok().body(size);
    }

    @GetMapping("members/{id}/account")
    public ResponseEntity<List<AccountDto>> findAccounts(@PathVariable Long id){
        List<AccountDto> accounts = accountService.findAccountsByMemberId(id);
        return ResponseEntity.ok().body(accounts);
    }



}