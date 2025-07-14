package gift.dto;

import gift.entity.Member;
import gift.entity.Role;
import gift.validator.ValidUpdateMemberRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@ValidUpdateMemberRequest
public record UpdateMemberRequest (
        @NotNull(message = "회원 id는 제시되어야합니다.")
        Long identifyNumber,

        @NotNull(message = "이메일은 제시되어야합니다.")
        @Email(message = "올바른 이메일 양식이 아닙니다.")
        String email,

        @NotNull(message = "비밀번호는 제시되어야합니다.") // 빈칸인 경우 비밀번호를 유지하나, null이 올 수는 없음
        String password,

        @NotNull(message = "권한은 제시되어야합니다.")
        Role authority
) {
        public static UpdateMemberRequest from(Member member) {
                return new UpdateMemberRequest(
                        member.getIdentifyNumber(),
                        member.getEmail(),
                        null,
                        member.getAuthority()
                );
        }
}
