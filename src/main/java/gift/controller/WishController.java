package gift.controller;

import gift.dto.AddWishItemRequest;
import gift.dto.WishItemResponse;
import gift.entity.Member;
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
        @LoginMember Member member
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                wishService.getWishListByMemberId(member.getIdentifyNumber())
                    .stream().map(wishItem -> WishItemResponse.from(wishItem)).toList()
            );
    }

    @PostMapping
    public ResponseEntity<WishItemResponse> addWishItem(
        @LoginMember Member member,
        @RequestBody @Valid AddWishItemRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                WishItemResponse.from(
                        wishService.addWishItem(member.getIdentifyNumber(), request.productId())
                )
            );
    }

    @DeleteMapping("/{wishItemId}")
    public ResponseEntity<Void> deleteWishItem(
        @LoginMember Member member,
        @PathVariable Long wishItemId
    ) {
        wishService.removeWishItemByWishId(member.getIdentifyNumber(), wishItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
