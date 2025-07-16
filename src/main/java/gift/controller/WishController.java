package gift.controller;

import gift.dto.AddWishItemRequest;
import gift.dto.AuthenticatedMember;
import gift.dto.WishItemResponse;
import gift.entity.Wish;
import gift.service.WishService;
import gift.validator.LoginMember;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<List<WishItemResponse>> getWishList(
        @LoginMember AuthenticatedMember member
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                wishService.getWishListByMemberId(member.id())
                    .stream().map(wishItem -> WishItemResponse.from(wishItem)).toList()
            );
    }

    @PostMapping
    public ResponseEntity<WishItemResponse> addWishItem(
        @LoginMember AuthenticatedMember member,
        @RequestBody @Valid AddWishItemRequest request
    ) {
        Wish created = wishService.addWishItem(member.id(), request.productId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/api/wish/" + created.getId())
            .body(WishItemResponse.from(created));
    }

    @DeleteMapping("/{wishItemId}")
    public ResponseEntity<Void> deleteWishItem(
        @LoginMember AuthenticatedMember member,
        @PathVariable Long wishItemId
    ) {
        wishService.removeWishItemByWishId(member.id(), wishItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
