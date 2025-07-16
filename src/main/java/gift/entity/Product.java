package gift.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Product {

    private static final List<String> prohibitedNames = List.of("카카오");

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean validated;

    @Column(nullable = false)
    private Boolean deleted = false;

    protected Product() {}

    public Product(Long id, String name, Integer price, String imageUrl, Boolean validated) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = validated;
    }

    // this constructor is used only for the repository row mapper
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

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Boolean isValidated() {
        return validated;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void applyPatch(String name, Integer price, String imageUrl) {
        if (name != null) {
            this.name = name;
        }
        if (price != null) {
            this.price = price;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        this.validated = checkValidatedByName(this.name);
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
