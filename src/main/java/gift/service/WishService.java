package gift.service;

import gift.entity.Product;
import gift.entity.WishItem;
import gift.repository.WishRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    public List<WishItem> getWishListByMemberId(Long memberId) {
        List<WishItem> wishList = wishRepository.getWishListByMemberId(memberId);
        return wishList.stream().map(
                wishItem -> wishItem.updateDetails(productService.getProductWhetherDeletedById(wishItem.productId()))
        ).toList();
    }

    public WishItem addWishItem(Long memberId, Long productId) {
        // 상품이 존재하는지 확인 및 반환
        Product product = productService.getProductById(productId);
        try {
            Optional<Long> optionalWishId = wishRepository.addWishItem(memberId, productId);
            if (optionalWishId.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "WishItem creation failed");
            }
            return WishItem.from(product).updateId(optionalWishId.get());
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "WishItem already exists for this product");
        }
    }

    public void removeWishItemByWishId(Long memberId, Long wishId) {
        if (!wishRepository.removeWishItemByMemberWishId(memberId, wishId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "WishItem not found");
        }
    }
}
