package gift.service;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(String name, Integer price, String imageUrl) {
        Product product = new Product(name, price, imageUrl);
        return productRepository.save(product);
    }

    // for normal users
    public Product getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndDeletedIsFalse(id);
        throwNotFoundIfTrue(optionalProduct.isEmpty());
        return optionalProduct.get();
    }

    // for md users
    public Product getProductWhetherDeletedById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        throwNotFoundIfTrue(optionalProduct.isEmpty());
        return optionalProduct.get();
    }

    // TODO : validated T/F로 나누지 말고, 위 처럼 whetherDeleted로 나누는 걸로 변경 (findAll 사용)
    public List<Product> getProductList(Boolean validated) {
        return productRepository.findAllByDeletedIsFalseAndValidated(validated);
    }

    public Product updateProductById(Long id, String name, Integer price, String imageUrl) {
        Product product = getProductById(id);
        product.applyPatch(name, price, imageUrl);
        return product;
    }

    public void setProductValidated(Long id, Boolean validated) {
        Product product = getProductById(id);
        product.setValidated(validated);
    }

    public void softDeleteProductById(Long id) {
        Product product = getProductById(id);
        product.setDeleted(true);
    }

    private void throwNotFoundIfTrue(boolean condition) {
        if (condition) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
