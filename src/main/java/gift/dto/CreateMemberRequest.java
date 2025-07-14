package gift.dto;

import gift.entity.Role;
import gift.validator.ValidNewMemberRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@ValidNewMemberRequest
public record CreateMemberRequest (
        @NotNull(message = "이메일은 제시되어야합니다.")
        @Email(message = "올바른 이메일 양식이 아닙니다.")
        String email,

        @NotNull(message = "비밀번호는 제시되어야합니다.") // 빈칸인 경우 비밀번호를 유지하나, null이 올 수는 없음
        String password,

        @NotNull(message = "권한은 제시되어야합니다.")
        Role authority
) implements NewMemberRequest {
    public static CreateMemberRequest empty() {
        return new CreateMemberRequest("", "", Role.ROLE_USER);
    }
}
