package dto;

import domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String email;
    private String openBankid;

    @Builder
    public MemberDto(Long id, String email, String openBankid) {
        this.id = id;
        this.email = email;
        this.openBankid = openBankid;

    }

    public static MemberDto of(Member member) {
        return MemberDto
                .builder()
                .email(member.getEmail())
                .id(member.getId())
                .openBankid(member.getOpenBankId())
                .build();

    }
}
