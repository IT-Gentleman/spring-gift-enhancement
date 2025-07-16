package gift.dto;

import gift.entity.Member;

public record MemberResponse (
        Long identifyNumber,
        String email,
        String authority
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getRole().name()
        );
    }
}
