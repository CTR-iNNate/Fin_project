package com.example.fintech001.service;

import com.example.fintech001.client.OpenBankApiClient;
import com.example.fintech001.client.OpenBankUtil;
import dto.AccountRequestDto;
import dto.BalanceRequestDto;
import dto.TokenRequestDto;
import dto.openbank.*;
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

    public OpenBankResponseToken requestToken(TokenRequestDto tokenRequestDto) {
    OpenBankRequestToken openBankRequestToken = OpenBankRequestToken.builder()
            .code(tokenRequestDto.getCode())
            .client_id(clientId)
            .client_secret(clientSecret)
            .redirect_uri(redirect_uri)
            .grant_type("authorization_code")
            .build();
        OpenBankResponseToken openBankResponseToken = openBankApiClient.requestToken(openBankRequestToken);
        return openBankResponseToken;
    }



    public OpenBankAccountSearchResponseDto findAccount(AccountRequestDto accountRequestDto){
        OpenBankAccountSearchRequestDto searchRequestDto = OpenBankAccountSearchRequestDto.builder().user_seq_no(accountRequestDto.getOpenBankId())
                .access_token(accountRequestDto.getAccessToken())
                .include_cancel_yn("N")
                .sort_order("Y")
                .build();
        return openBankApiClient.requestAccountList(searchRequestDto);
    }


    public OpenBankBalanceResponseDto findBalance(BalanceRequestDto balanceRequestDto){

        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder()
                .accessToken(balanceRequestDto.getAccessToken())
                .fintech_use_num(balanceRequestDto.getFintechUseNum())
                .bank_tran_id(OpenBankUtil.getRandomNumber(useCode + "U"))
                .tran_dtime(OpenBankUtil.getTransTime())
                .build();

        OpenBankBalanceResponseDto openBankBalanceResponseDto = openBankApiClient.requestBalance(openBankBalanceRequestDto);
        return openBankBalanceResponseDto;
    }


    public OpenBankUserInfoResponseDto findOpenBankUserInfo(OpenBankUserInfoRequestDto openBankUserInfoRequestDto){
        return openBankApiClient.requestOpenBankUserInfo(openBankUserInfoRequestDto);
    }
}



