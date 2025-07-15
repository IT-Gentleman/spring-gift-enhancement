package gift.repository;

import gift.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdentifyNumber(Long identifyNumber);
    Optional<Member> findByEmail(String email);
    Integer deleteByIdentifyNumber(Long identifyNumber);
}
