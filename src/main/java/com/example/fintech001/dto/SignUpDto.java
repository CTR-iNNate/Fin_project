package com.example.fintech001.dto;

import com.example.fintech001.domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class SignUpDto {

    private String email;
    private String password;


    @Builder
    public SignUpDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Member toEntity() {
        return Member.builder().email(this.email).password(this.password).build();
    }


}