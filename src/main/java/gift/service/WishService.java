package gift.service;

import gift.entity.Product;
import gift.entity.WishItem;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    public List<WishItem> getWishListByMemberId(Long memberId) {
        return wishRepository.findAllByMemberId(memberId);
    }

    public WishItem addWishItem(Long memberId, Long productId) {
        // 상품이 존재하는지 확인 및 반환
        Product product = productService.getProductById(productId);
        try {
            WishItem wishItem = new WishItem(memberId, product);
            return wishRepository.save(wishItem);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "WishItem already exists for this product");
        }
    }

    public void removeWishItemByWishId(Long memberId, Long wishId) {
        if (!wishRepository.existsByIdAndMemberId(wishId, memberId)) {
            // 본인소유가 아닌 wish의 경우는 hiding 처리됨
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "WishItem not found");
        }
        wishRepository.deleteByIdAndMemberId(wishId, memberId);
    }
}
