package gift.repository;

import gift.entity.WishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<WishItem, Long> {

    List<WishItem> findAllByMemberIdentifyNumber(Long memberId);
    Integer removeByMemberIdentifyNumberAndId(Long memberId, Long wishId);
}
