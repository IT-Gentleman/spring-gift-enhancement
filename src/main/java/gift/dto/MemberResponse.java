package gift.dto;

import gift.entity.Member;

public record MemberResponse(
        Long id,
        String email,
        String role
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getRole().name()
        );
    }
}
