package domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class OpenBankToken extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long memberId;
    @Column(length = 1000)
    @NotNull
    private String accessToken;
    @Column(length = 1000)
    @NotNull
    private String refreshToken;
    @NotNull
    private Long expiresIn;
    private String openBankId;

    @Builder
    public OpenBankToken(Long id, Long memberId, String accessToken, String refreshToken, Long expiresIn, String openBankId) {
        this.id = id;
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.openBankId = openBankId;
    }

}