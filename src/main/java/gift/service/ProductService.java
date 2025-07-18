package gift.service;

import gift.dto.NewProductCommand;
import gift.dto.ProductDto;
import gift.dto.UpdateProductCommand;
import gift.entity.Product;
import gift.exception.NotFoundException;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductDto createProduct(NewProductCommand command) {
        Product product = new Product(command.name(), command.price(), command.imageUrl());
        return ProductDto.from(productRepository.save(product));
    }

    // for normal users
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = findProductByIdAndNotDeleted(id);
        return ProductDto.from(product);
    }

    // for md users
    @Transactional(readOnly = true)
    public ProductDto getProductWhetherDeletedById(Long id) {
        Product product = findProductByIdIncludingDeleted(id);
        return ProductDto.from(product);
    }

    // TODO : validated T/F로 나누지 말고, 위 처럼 whetherDeleted로 나누는 걸로 변경 (findAll 사용)
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductList(Boolean validated, Pageable pageable) {
        Page<Product> pageProduct = productRepository.findAllByDeletedIsFalseAndValidated(validated,
                pageable);
        return pageProduct.map(ProductDto::from);
    }

    @Transactional
    public ProductDto updateProductById(UpdateProductCommand command) {
        Product product = findProductByIdAndNotDeleted(command.id());
        product.applyPatch(command.name(), command.price(), command.imageUrl());
        return ProductDto.from(product);
    }

    @Transactional
    public void setProductValidated(Long id, Boolean validated) {
        Product product = findProductByIdAndNotDeleted(id);
        product.setValidated(validated);
    }

    @Transactional
    public void softDeleteProductById(Long id) {
        Product product = findProductByIdAndNotDeleted(id);
        product.setDeleted(true);
    }

    protected Product findProductByIdAndNotDeleted(Long id) {
        return productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    protected Product findProductByIdIncludingDeleted(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
