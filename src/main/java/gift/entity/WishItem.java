package gift.entity;

import java.time.LocalDateTime;

public record WishItem (
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Boolean deleted,
        LocalDateTime addedAt
) {
    public WishItem(Long id, Long productId, LocalDateTime addedAt) {
        this(id, productId, null, null, null, addedAt);
    }

    public WishItem updateId(Long id) {
        return new WishItem(id, productId, productName, productImageUrl, deleted, addedAt);
    }

    public WishItem updateDetails(Product product) {
        return new WishItem(id, product.getId(), product.getName(), product.getImageUrl(), product.getDeleted(), addedAt);
    }

    public static WishItem from(Product product) {
        return new WishItem(null, product.getId(), product.getName(), product.getImageUrl(), product.getDeleted(), LocalDateTime.now());
    }
}
