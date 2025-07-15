package gift.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wish")
public class WishItem {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    Member member;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "added_at", nullable = false)
    LocalDateTime addedAt;

    protected WishItem() {}

    public WishItem(Long id, Member member, Product product, LocalDateTime addedAt) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.addedAt = addedAt;
    }

    public WishItem(Long memberId, Product product) {
        this.member = new Member(memberId, null, null, null);
        this.product = product;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

}
