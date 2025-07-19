package gift.controller;

import gift.dto.LoginMemberCommand;
import gift.dto.LoginMemberRequest;
import gift.dto.LoginMemberResponse;
import gift.dto.NewMemberCommand;
import gift.dto.RegisterMemberRequest;
import gift.dto.RegisterMemberResponse;
import gift.service.AuthService;
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
    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterMemberResponse> createMember(
            @Valid @RequestBody RegisterMemberRequest registerMemberRequest
    ) {
        NewMemberCommand newMemberCommand = new NewMemberCommand(
                registerMemberRequest.email(),
                registerMemberRequest.password()
        );
        memberService.createMember(newMemberCommand);

        LoginMemberCommand loginMemberCommand = new LoginMemberCommand(
                registerMemberRequest.email(),
                registerMemberRequest.password()
        );
        String token = authService.login(loginMemberCommand);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterMemberResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(
            @Valid @RequestBody LoginMemberRequest loginMemberRequest
    ) {
        LoginMemberCommand loginMemberCommand = new LoginMemberCommand(
                loginMemberRequest.email(),
                loginMemberRequest.password()
        );
        String token = authService.login(loginMemberCommand);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new LoginMemberResponse(token));
    }
}
