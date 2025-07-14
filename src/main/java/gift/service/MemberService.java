package gift.service;

import gift.entity.Member;
import gift.entity.Role;
import gift.exception.InvalidCredentialsException;
import gift.repository.MemberRepository;
import gift.token.JwtTokenProvider;
import gift.util.BCryptEncryptor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Member createMember(String email, String rawPassword) {
        String encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        Member member = new Member(email, encodedPassword);
        Optional<Long> optionalIdentifyNumber = memberRepository.createMember(member);
        if (optionalIdentifyNumber.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Member creation failed");
        }
        return member.updateIdentifyNumber(optionalIdentifyNumber.get());
    }

    public String login(String email, String rawPassword) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty() || !BCryptEncryptor.matches(rawPassword, optionalMember.get().getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtTokenProvider.createToken(optionalMember.get());
    }

    public List<Member> getMemberList() {
        return memberRepository.getMemberList();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    public Member updateSelectivelyMember(Long id, String email, String password, Role authority) {
        Member member = getMemberById(id);
        if (!password.isEmpty()) {
            password = BCryptEncryptor.encrypt(password);
        }
        throwNotFoundIfTrue(!memberRepository.updateMember(member.applyPatch(email, password, authority)));
        return member;
    }

    public void deleteMember(Long id) {
        throwNotFoundIfTrue(!memberRepository.deleteMember(id));
    }

    private void throwNotFoundIfTrue(boolean condition) {
        if (condition) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Member getMemberFromToken(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String username = jwtTokenProvider.getUsername(token);
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (optionalMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return optionalMember.get();
    }
}
