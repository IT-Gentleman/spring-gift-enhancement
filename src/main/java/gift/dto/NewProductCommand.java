package gift.dto;

public record NewProductCommand(
        String name,
        Integer price,
        String imageUrl
) {

}
