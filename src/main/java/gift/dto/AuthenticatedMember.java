package gift.dto;

import gift.entity.Member;
import gift.entity.Role;

public record AuthenticatedMember (
        Long id,
        String email,
        Role authority
) {
    public static AuthenticatedMember from(Member member) {
        return new AuthenticatedMember(member.getIdentifyNumber(), member.getEmail(), member.getAuthority());
    }
}
