package gift.dto;

import gift.validator.ValidNewMemberRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@ValidNewMemberRequest
public record RegisterMemberRequest(
        @NotNull(message = "이메일은 제시되어야합니다.")
        @Email(message = "올바른 이메일 양식이 아닙니다.")
        String email,

        @NotNull(message = "비밀번호는 제시되어야합니다.")
        String password
) implements NewMemberRequest {
    public static RegisterMemberRequest empty() {
        return new RegisterMemberRequest("", "");
    }
}
