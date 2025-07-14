package gift.dto;

import gift.entity.WishItem;

import java.time.LocalDateTime;

public record WishItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Boolean deleted,
        LocalDateTime addedAt
) {
    public static WishItemResponse from(WishItem wishItem) {
        return new WishItemResponse(
                wishItem.id(),
                wishItem.productId(),
                wishItem.productName(),
                wishItem.productImageUrl(),
                wishItem.deleted(),
                wishItem.addedAt()
        );
    }
}
