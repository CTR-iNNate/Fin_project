package client;

import dto.openbank.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenBankApiClient {
    private final RestTemplate restTemplate;
    private static final String base_url = "https://testapi.openbanking.or.kr";
    private static final String successCode = "A0000";


    public OpenBankResponseToken requestToken(OpenBankRequestToken openBankRequestToken) {
        HttpHeaders httpHeaders = generateHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        HttpEntity httpEntity = generateHttpEntityWithBody(httpHeaders, openBankRequestToken.toMultiValueMap());

        OpenBankResponseToken openBankResponseToken = restTemplate.exchange(base_url + "/oauth/2.0/token", HttpMethod.POST, httpEntity, OpenBankResponseToken.class).getBody();

        if (isErrorCode(openBankResponseToken.getRsp_code())) {
            log.error("error code : {}, error msg : {}", openBankResponseToken.getRsp_code(), openBankResponseToken.getRsp_message());
            throw new RuntimeException(openBankResponseToken.getRsp_message());
        }
        return openBankResponseToken;

    }

    private HttpEntity generateHttpEntityWithBody(HttpHeaders httpHeaders, MultiValueMap body) {
        return new HttpEntity<>(body, httpHeaders);
    }


    public OpenBankAccountSearchResponseDto requestAccountList(OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto) {
        String url = base_url + "/v2.0/account/list";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankAccountSearchRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build();

        OpenBankAccountSearchResponseDto bankAccountSearchResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankAccountSearchResponseDto.class).getBody();
        if (isErrorCode(bankAccountSearchResponseDto.getRsp_code())) {
            log.error("error code : {}, error msg : {}", bankAccountSearchResponseDto.getRsp_code(), bankAccountSearchResponseDto.getRsp_message());
            throw new RuntimeException(bankAccountSearchResponseDto.getRsp_message());
        }
        return bankAccountSearchResponseDto;
    }

    /**
     * 잔액조회
     */
    public OpenBankBalanceResponseDto requestBalance(OpenBankBalanceRequestDto openBankBalanceRequestDto) {
        String url = base_url + "/v2.0/account/balance/fin_num";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankBalanceRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build();

        OpenBankBalanceResponseDto openBankBalanceResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankBalanceResponseDto.class).getBody();
        if (isErrorCode(openBankBalanceResponseDto.getRsp_code())) {
            log.error("error code : {}, error msg : {}", openBankBalanceResponseDto.getRsp_code(), openBankBalanceResponseDto.getRsp_message());
            throw new RuntimeException(openBankBalanceResponseDto.getRsp_message());
        }
        return openBankBalanceResponseDto;
    }


    public OpenBankUserInfoResponseDto requestOpenBankUserInfo(OpenBankUserInfoRequestDto openBankUserInfoRequestDto){
        String url = base_url+"/v2.0/user/me";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankUserInfoRequestDto.getAccessToken()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", openBankUserInfoRequestDto.getOpenBankId())
                .build();
        OpenBankUserInfoResponseDto openBankUserInfoResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankUserInfoResponseDto.class).getBody();

        if (isErrorCode(openBankUserInfoResponseDto.getRsp_code())){
            log.error("error code : {}, error msg : {}", openBankUserInfoResponseDto.getRsp_code(), openBankUserInfoResponseDto.getRsp_message());
            throw new RuntimeException(openBankUserInfoResponseDto.getRsp_message());
        }
        return openBankUserInfoResponseDto;
    }


    private HttpEntity generateHttpEntity(HttpHeaders httpHeaders) {
        return new HttpEntity<>(httpHeaders);
    }



    private HttpHeaders generateHeader(String name, String val) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if(name.equals("Authorization")) {
            httpHeaders.add(name, "Bearer "+val);
            return httpHeaders;
        }

        httpHeaders.add(name, val);
        return httpHeaders;

    }

    private boolean isErrorCode(String code) {
        if(code==null) return false;
        if(code.equals(successCode)){
            return false;

        }
        return true;
    }




}
