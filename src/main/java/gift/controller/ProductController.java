package gift.controller;

import gift.dto.CreateProductRequest;
import gift.dto.PatchProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        Product created = productService.createProduct(
            request.name(),
            request.price(),
            request.imageUrl()
        );
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/api/products/" + created.getId())
            .body(ProductResponse.from(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable Long id
    ) {
        Product product = productService.getProductById(id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ProductResponse.from(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getProductList(true, pageable);
        Page<ProductResponse> response = products.map(ProductResponse::from);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductById(
        @PathVariable Long id,
        @Valid @RequestBody PatchProductRequest patch
    ) {
        Product product = productService.updateProductById(
            id,
            patch.name(),
            patch.price(),
            patch.imageUrl()
        );

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ProductResponse.from(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(
        @PathVariable Long id
    ) {
        productService.softDeleteProductById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
