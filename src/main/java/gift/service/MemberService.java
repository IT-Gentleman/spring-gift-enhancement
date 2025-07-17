package gift.service;

import gift.dto.AuthenticatedMember;
import gift.dto.UpdateMemberResponse;
import gift.entity.Member;
import gift.entity.Role;
import gift.exception.ConflictException;
import gift.exception.InvalidCredentialsException;
import gift.exception.NotFoundException;
import gift.repository.MemberRepository;
import gift.token.JwtTokenProvider;
import gift.util.BCryptEncryptor;
import gift.util.PasswordUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public Member createMember(String email, String rawPassword) {
        checkValidMemberUpdate(email, null);

        String encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        Member member = new Member(email, encodedPassword);
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public String login(String email, String rawPassword) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty() || !BCryptEncryptor.matches(rawPassword, optionalMember.get().getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtTokenProvider.createToken(optionalMember.get());
    }

    @Transactional(readOnly = true)
    public Page<Member> getMemberList(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found"));
    }

    @Transactional
    public UpdateMemberResponse updateSelectivelyMember(Long id, String email, Boolean resetPassword, Role authority) {
        // getMemberById는 readOnly=true이나, 이미 활성화된 Transaction(readOnly=false)에 참여하는 형태
        Member member = getMemberById(id);
        checkValidMemberUpdate(email, member.getId());
        String rawPassword = null;
        String encodedPassword = null;
        if (resetPassword) {
            rawPassword = PasswordUtility.generateRandomPassword(15);
            encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        }
        member.applyPatch(email, encodedPassword, authority);
        return new UpdateMemberResponse(member, rawPassword);
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new NotFoundException("Member not found");
        }
        memberRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public AuthenticatedMember getAuthenticationFromToken(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String username = jwtTokenProvider.getUsername(token);
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (optionalMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return AuthenticatedMember.from(optionalMember.get());
    }

    private void checkValidMemberUpdate(String email, Long memberId) {
        if (!isEmailUsable(email, memberId)) {
            throw new ConflictException("Email already in use");
        }
    }

    // 호출 메소드의 Transaction에 참여
    private boolean isEmailUsable(String email, Long memberId) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        // 해당 이메일을 사용중인 멤버가 없거나, 이를 요청한 회원이 해당 이메일의 소유자일 경우 (즉, 이메일 변경이 아님)
        return optionalMember.isEmpty() || optionalMember.get().getId().equals(memberId);
    }
}
