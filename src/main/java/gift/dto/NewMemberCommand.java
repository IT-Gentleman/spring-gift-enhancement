package gift.dto;

import gift.entity.Role;

public record NewMemberCommand(
        String email,
        String password,
        Role role
) {

    public NewMemberCommand(String email, String password) {
        this(email, password, Role.ROLE_USER);
    }
}
