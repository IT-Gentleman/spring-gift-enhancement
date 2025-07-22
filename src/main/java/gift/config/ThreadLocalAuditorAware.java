package gift.config;

import gift.entity.Member;
import gift.util.LoginMemberContextHolder;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("threadLocalAuditorAware")
public class ThreadLocalAuditorAware implements AuditorAware<Member> {

    @Override
    public Optional<Member> getCurrentAuditor() {
        Long userId = LoginMemberContextHolder.get();
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.of(Member.emptyOfId(userId));
    }
}
