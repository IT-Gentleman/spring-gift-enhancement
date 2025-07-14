package gift.repository;

import gift.entity.Member;
import gift.entity.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<Member> memberRowMapper = (rs, rowNum) -> new Member(
        rs.getLong("identify_number"),
        rs.getString("email"),
        rs.getString("password"),
        Role.valueOf(rs.getString("authority"))
    );

    public MemberRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Long> createMember(Member member) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into member (email, password, authority) values (:email, :password, :authority)")
            .param("email", member.getEmail())
            .param("password", member.getPassword())
            .param("authority", member.getAuthority().name())
            .update(keyHolder);
        return Optional.ofNullable(keyHolder.getKeyAs(Long.class));
    }

    public List<Member> getMemberList() {
        return jdbcClient.sql("select * from member")
            .query(memberRowMapper)
            .list();
    }

    public Boolean checkEmailExists(String email) {
        return jdbcClient.sql("select count(*) from member where email = :email")
            .param("email", email)
            .query(Long.class)
            .single() == 1;
    }

    public Optional<Member> findByEmail(String email) {
        return jdbcClient.sql("select * from member where email = :email")
            .param("email", email)
            .query(memberRowMapper)
            .optional();
    }

    public Optional<Member> findById(Long id) {
        return jdbcClient.sql("select * from member where identify_number = :id")
            .param("id", id)
            .query(memberRowMapper)
            .optional();
    }

    public boolean updateMember(Member member) {
        return jdbcClient.sql("update member set email = :email, password = :password, authority = :authority where identify_number = :id")
            .param("id", member.getIdentifyNumber())
            .param("email", member.getEmail())
            .param("password", member.getPassword())
            .param("authority", member.getAuthority().name())
            .update() == 1;
    }

    public boolean deleteMember(Long id) {
        return jdbcClient.sql("delete from member where identify_number = :id")
            .param("id", id)
            .update() == 1;
    }

}
