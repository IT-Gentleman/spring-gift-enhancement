package gift.repository;

import gift.entity.WishItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WishRepository {

    private final JdbcClient jdbcClient;

    private final RowMapper<WishItem> wishItemRowMapper = (rs, rowNum) -> new WishItem(
            rs.getLong("id"),
            rs.getLong("product_id"),
            rs.getTimestamp("added_at").toLocalDateTime()
    );

    public WishRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<WishItem> getWishListByMemberId(Long memberId) {
        return jdbcClient.sql("select id, product_id, added_at from wish where member_id = :memberId")
                .param("memberId", memberId)
                .query(wishItemRowMapper)
                .list();
    }

    public Optional<Long> addWishItem(Long memberId, Long productId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into wish (member_id, product_id) values (:memberId, :productId)")
                .param("memberId", memberId)
                .param("productId", productId)
                .update(keyHolder, new String[]{"id"});
        return Optional.ofNullable(keyHolder.getKeyAs(Long.class));
    }

    public boolean removeWishItemByMemberWishId(Long memberId, Long wishId) {
        return jdbcClient.sql("delete from wish where id = :wishId and member_id = :memberId")
                .param("wishId", wishId)
                .param("memberId", memberId)
                .update() == 1;
    }
}
