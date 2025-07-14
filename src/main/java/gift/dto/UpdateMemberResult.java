package gift.dto;

import gift.entity.Member;

import java.util.Optional;

public record UpdateMemberResult (
        Member member,
        Optional<String> temporalPassword
) {
}
