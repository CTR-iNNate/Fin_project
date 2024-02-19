package com.example.fintech001.controller;

import com.example.fintech001.service.OpenBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class fintechApi {

    @Value("${openbank.client-id}")
    private String clientId;

    @Value("${openbank.useCode}")
    private String useCode;

    @Value("${openbank.client-secret")
    private String clientSecret;

    @Value("${openbank.access-token}")
    private String access_token;


    private final OpenBankService openBankService;




    @GetMapping("/")
    public String TestId(Model model){

        model.addAttribute("clientId", clientId);
        model.addAttribute("access_token", access_token);




        return "/home";


    }
}
