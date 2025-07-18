package gift.controller;

import gift.dto.AddWishRequest;
import gift.dto.AuthenticatedMember;
import gift.dto.PageResponse;
import gift.dto.WishResponse;
import gift.entity.Wish;
import gift.service.WishService;
import gift.validator.LoginMember;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<WishResponse>> getWishList(
            @LoginMember AuthenticatedMember member,
            Pageable pageable
    ) {
        Page<Wish> wishPage = wishService.getWishListByMemberId(member.id(), pageable);
        Page<WishResponse> wishResponsePage = wishPage.map(wish -> WishResponse.from(wish));
        PageResponse<WishResponse> pageResponse = PageResponse.from(wishResponsePage);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageResponse);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWishItem(
            @LoginMember AuthenticatedMember member,
            @RequestBody @Valid AddWishRequest request
    ) {
        Wish created = wishService.addWishItem(member.id(), request.productId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/wish/" + created.getId())
                .body(WishResponse.from(created));
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
