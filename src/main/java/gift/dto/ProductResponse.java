package gift.dto;

public record ProductResponse(
        Long id,
        String name,
        Integer price,
        String imageUrl,
        Boolean validated
) {

    public static ProductResponse from(ProductDto productDto) {
        return new ProductResponse(
                productDto.id(),
                productDto.name(),
                productDto.price(),
                productDto.imageUrl(),
                productDto.validated()
        );
    }
}