package gift.service;

import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.WishRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<Wish> getWishListByMemberId(Long memberId) {
        return wishRepository.findAllByMemberId(memberId);
    }

    @Transactional
    public Wish addWishItem(Long memberId, Long productId) {
        // 상품이 존재하는지 확인 및 반환
        Product product = productService.getProductById(productId);

        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wish already exists for this product");
        }
        Wish wish = new Wish(memberId, product);
        return wishRepository.save(wish);
    }

    @Transactional
    public void removeWishItemByWishId(Long memberId, Long wishId) {
        if (!wishRepository.existsByIdAndMemberId(wishId, memberId)) {
            // 본인소유가 아닌 wish의 경우는 hiding 처리됨
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wish not found");
        }
        wishRepository.deleteByIdAndMemberId(wishId, memberId);
    }
}
