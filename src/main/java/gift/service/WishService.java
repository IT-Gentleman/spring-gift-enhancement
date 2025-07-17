package gift.service;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.ConflictException;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public Page<Wish> getWishListByMemberId(Long memberId, Pageable pageable) {
        return wishRepository.findAllByMemberId(memberId, pageable);
    }

    @Transactional
    public Wish addWishItem(Long memberId, Long productId) {
        // 상품이 존재하는지 확인 및 반환
        Product product = productService.getProductById(productId);

        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ConflictException("Wish already exists for this product");
        }
        Wish wish = new Wish(Member.emptyOfId(memberId), product);
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
