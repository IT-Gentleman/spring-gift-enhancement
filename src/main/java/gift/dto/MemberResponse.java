package gift.dto;

import gift.entity.Member;

public record MemberResponse (
        Long identifyNumber,
        String email,
        String authority
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getIdentifyNumber(),
                member.getEmail(),
                member.getAuthority().name()
        );
    }
}
