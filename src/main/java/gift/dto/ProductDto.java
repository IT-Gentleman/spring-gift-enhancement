package gift.dto;

import gift.entity.Product;

public record ProductDto(
        Long id,
        String name,
        Integer price,
        String imageUrl,
        Boolean validated,
        Boolean deleted
) {

    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.isValidated(),
                product.isDeleted()
        );
    }
}
