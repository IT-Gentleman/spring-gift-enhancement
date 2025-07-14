package gift.entity;

import java.util.List;

public class Product {

    private static final List<String> prohibitedNames = List.of("카카오");

    private Long id;
    private String name;
    private Integer price;
    private String imageUrl;
    private Boolean validated;
    private Boolean deleted = false;

    public Product() {}

    public Product(Long id, String name, Integer price, String imageUrl, Boolean validated) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = validated;
    }

    // this constructor is used only for the repository
    public Product(Long id, String name, Integer price, String imageUrl, Boolean validated, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = validated;
        this.deleted = deleted;
    }

    public Product(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = checkValidatedByName(name);
    }

    public Product updateId(Long id) {
        return new Product(id, name, price, imageUrl, validated);
    }

    public Long getId() {
        return id;
    }

    public Product updateName(String name) {
        return new Product(id, name, price, imageUrl, validated);
    }

    public String getName() {
        return name;
    }

    public Product updatePrice(Integer price) {
        return new Product(id, name, price, imageUrl, validated);
    }

    public Integer getPrice() {
        return price;
    }

    public Product updateImageUrl(String imageUrl) {
        return new Product(id, name, price, imageUrl, validated);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Product updateValidated(Boolean validated) {
        return new Product(id, name, price, imageUrl, validated);
    }

    public Boolean getValidated() {
        return validated;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Product applyPatch(String name, Integer price, String imageUrl) {
        String updatedName = name != null ? name : this.name;
        Integer updatedPrice = price != null ? price : this.price;
        String updatedimageUrl = imageUrl != null ? imageUrl : this.imageUrl;
        Boolean updatedValidated = checkValidatedByName(updatedName);
        return new Product(id, updatedName, updatedPrice, updatedimageUrl, updatedValidated);
    }

    private Boolean checkValidatedByName(String name) {
        for (String prohibitedName : prohibitedNames) {
            if (name.contains(prohibitedName)) {
                return false;
            }
        }
        return true;
    }
}
