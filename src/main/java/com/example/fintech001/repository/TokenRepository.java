package com.example.fintech001.repository;

import com.example.fintech001.domain.OpenBankToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<OpenBankToken, Long> {
    Optional<OpenBankToken> findOpenBankTokenByMemberId(Long memberId);

    boolean existsOpenBankTokenByMemberId(Long aLong);
}