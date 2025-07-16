package gift.dto;

import gift.entity.Wish;

import java.time.LocalDateTime;

public record WishItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Boolean deleted,
        LocalDateTime addedAt
) {
    public static WishItemResponse from(Wish wish) {
        return new WishItemResponse(
                wish.getId(),
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getImageUrl(),
                wish.getProduct().isDeleted(),
                wish.getAddedAt()
        );
    }
}
