package gift.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Member {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long identifyNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role authority;

    @OneToMany(mappedBy = "member")
    //@JoinColumn(name = "member_id", nullable = false)
    private List<WishItem> wishlist;

    protected Member() {}

    public Member(Long identifyNumber, String email, String password, Role authority) {
        this.identifyNumber = identifyNumber;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    public Member(String email, String password) {
        this(null, email, password, Role.ROLE_USER);
    }

    public Long getIdentifyNumber() {
        return identifyNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Role getAuthority() {
        return authority;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void applyPatch(String email, String password, Role authority) {
        if (email != null) {
            this.email = email;
        }
        if (password != null) {
            this.password = password;
        }
        if (authority != null) {
            this.authority = authority;
        }
    }
}
