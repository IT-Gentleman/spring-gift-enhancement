package gift.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    protected Wish() {}

    public Wish(Long id, Member member, Product product, LocalDateTime addedAt) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.addedAt = addedAt;
    }

    public Wish(Long memberId, Product product) {
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
