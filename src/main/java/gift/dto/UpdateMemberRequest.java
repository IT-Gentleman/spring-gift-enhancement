package gift.dto;

import gift.entity.Member;
import gift.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

// for Admin Page. Authority is required.
public record UpdateMemberRequest (
        @NotNull(message = "회원 id는 제시되어야합니다.")
        Long identifyNumber,

        @NotNull(message = "이메일은 제시되어야합니다.")
        @Email(message = "올바른 이메일 양식이 아닙니다.")
        String email,

        // 비밀번호는 별도 '초기화' 로직으로 분리
        @NotNull(message = "비밀번호 초기화여부는 제시되어야합니다.")
        Boolean resetPassword,

        @NotNull(message = "권한은 제시되어야합니다.")
        Role authority
) {
        public static UpdateMemberRequest from(Member member) {
                return new UpdateMemberRequest(
                        member.getId(),
                        member.getEmail(),
                        false,
                        member.getRole()
                );
        }
}
