package gift.controller;

import gift.dto.CreateProductRequest;
import gift.dto.PatchProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getProductList(true);
        List<ProductResponse> response = products.stream()
                .map(product -> ProductResponse.from(product))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductById(
            @PathVariable Long id,
            @Valid @RequestBody PatchProductRequest patch
    ) {
        Product product = productService.updateSelectivelyProductById(
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
