package gift.controller;

import gift.dto.LoginMemberRequest;
import gift.dto.LoginMemberResponse;
import gift.dto.RegisterMemberRequest;
import gift.dto.RegisterMemberResponse;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterMemberResponse> createMember(
            @Valid @RequestBody RegisterMemberRequest registerMemberRequest
    ) {
        memberService.createMember(registerMemberRequest.email(), registerMemberRequest.password());

        String token = memberService.login(
                registerMemberRequest.email(),
                registerMemberRequest.password()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterMemberResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(
            @Valid @RequestBody LoginMemberRequest loginMemberRequest
    ) {
        String token = memberService.login(
                loginMemberRequest.email(),
                loginMemberRequest.password()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new LoginMemberResponse(token));
    }
}
