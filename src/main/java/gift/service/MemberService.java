package gift.service;

import gift.dto.AuthenticatedMember;
import gift.dto.UpdateMemberResult;
import gift.entity.Member;
import gift.entity.Role;
import gift.exception.InvalidCredentialsException;
import gift.repository.MemberRepository;
import gift.token.JwtTokenProvider;
import gift.util.BCryptEncryptor;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Member createMember(String email, String rawPassword) {
        checkValidMemberUpdate(email, null);

        String encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        Member member = new Member(email, encodedPassword);
        return memberRepository.save(member);
    }

    public String login(String email, String rawPassword) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty() || !BCryptEncryptor.matches(rawPassword, optionalMember.get().getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtTokenProvider.createToken(optionalMember.get());
    }

    public List<Member> getMemberList() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    public UpdateMemberResult updateSelectivelyMember(Long id, String email, Boolean resetPassword, Role authority) {
        Member member = getMemberById(id);
        checkValidMemberUpdate(email, member.getId());
        String rawPassword = null;
        String encodedPassword = null;
        if (resetPassword) {
            rawPassword = generateRandomPassword(10);
            encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        }
        member.applyPatch(email, encodedPassword, authority);
        return new UpdateMemberResult(member, Optional.ofNullable(rawPassword));
    }

    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        memberRepository.deleteById(id);
    }

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }

    private boolean isEmailUsable(String email, Long memberId) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        // 해당 이메일을 사용중인 멤버가 없거나, 이를 요청한 회원이 해당 이메일의 소유자일 경우 (즉, 이메일 변경이 아님)
        return optionalMember.isEmpty() || optionalMember.get().getId().equals(memberId);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void throwNotFoundIfTrue(boolean condition) {
        if (condition) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
