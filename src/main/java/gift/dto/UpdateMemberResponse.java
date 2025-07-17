package gift.dto;

import gift.entity.Member;

import java.util.Optional;

public record UpdateMemberResponse(
        Member member,
        String temporalPassword
) {
}
