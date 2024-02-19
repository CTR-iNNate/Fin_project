package com.example.fintech001.service;

import client.OpenBankApiClient;
import dto.TokenRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenBankService {
    private final String useCode;
    private final String clientId;
    private final String clientSecret;
    private final String redirect_uri;
    private final OpenBankApiClient openBankApiClient;

    public OpenBankService(@Value("${openbank.useCode}") String useCode, @Value("${openbank.client-id}") String clientId,
                           @Value("${openbank.client-secret") String clientSecret, @Value("${openbank.redirect-uri}") String redirect_uri,
                           OpenBankApiClient openBankApiClient) {
        this.useCode = useCode;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirect_uri = redirect_uri;
        this.openBankApiClient = openBankApiClient;

    }

    public OpenBankRequestToken requestToken(TokenRequestDto tokenRequestDto) {
    OpenBankRequestToken openBankRequestToken = OpenBankRequestToken.

    }


}
