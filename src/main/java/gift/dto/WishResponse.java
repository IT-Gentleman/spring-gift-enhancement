package gift.dto;

import gift.entity.Wish;
import java.time.LocalDateTime;

public record WishResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Boolean deleted,
        LocalDateTime addedAt
) {

    public static WishResponse from(Wish wish) {
        return new WishResponse(
                wish.getId(),
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getImageUrl(),
                wish.getProduct().isDeleted(),
                wish.getAddedAt()
        );
    }
}
