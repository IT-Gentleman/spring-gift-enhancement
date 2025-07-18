package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    // non-argument constructor for JPA
    protected Product() {
    }

    // all arguments constructor for test code
    public Product(Long id, String name, Integer price, String imageUrl, Boolean validated,
            Boolean deleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = validated;
        this.deleted = deleted;
    }

    // constructor for product creation. use as a factory method
    public Product(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.validated = checkValidatedByName(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
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
