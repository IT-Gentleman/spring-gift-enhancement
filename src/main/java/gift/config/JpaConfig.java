package gift.config;

import gift.entity.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Profile("!test")
public class JpaConfig {

    @Bean
    public AuditorAware<Member> auditorAware() {
        return new ThreadLocalAuditorAware();
    }
}