package com.example.fintech001.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @Column(unique = true)
    private String openBankCi;
    @NotNull
    private String password;
    @Column(unique = true)
    private String openBankId;

    @Builder
    public Member(Long id, String email, String password, String openBankId, String openBankCi) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.openBankId = openBankId;
        this.openBankCi = openBankCi;
    }

    public void updateOpenBankId(String openBankId){
        this.openBankId = openBankId;
    }
    public void updateOpenBankCi(String openBankCi){
        this.openBankCi = openBankCi;
    }

    public boolean isValidPassword(String password){
        if (!this.password.equals(password)){
            return false;
        }
        return true;
    }

    public boolean hasOpenBankCi(){
        if (this.openBankCi == null || this.openBankCi.isBlank()){
            return false;
        }
        return true;
    }

    public boolean hasOpenBankId(){
        if (this.openBankId == null || this.openBankId.isBlank()){
            return false;
        }
        return true;
    }

}