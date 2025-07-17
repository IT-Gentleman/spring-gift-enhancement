package gift.controller;

import gift.dto.AddWishItemRequest;
import gift.dto.AuthenticatedMember;
import gift.dto.PageResponse;
import gift.dto.WishItemResponse;
import gift.entity.Wish;
import gift.service.WishService;
import gift.validator.LoginMember;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<WishItemResponse>> getWishList(
        @LoginMember AuthenticatedMember member,
        Pageable pageable
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                PageResponse.from(
                    wishService.getWishListByMemberId(member.id(), pageable)
                        .map(WishItemResponse::from)
                )
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
