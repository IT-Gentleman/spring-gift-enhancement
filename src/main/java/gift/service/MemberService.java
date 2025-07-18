package gift.service;

import gift.dto.AuthenticatedMember;
import gift.dto.LoginMemberCommand;
import gift.dto.MemberDto;
import gift.dto.NewMemberCommand;
import gift.dto.UpdateMemberCommand;
import gift.entity.Member;
import gift.exception.ConflictException;
import gift.exception.InvalidCredentialsException;
import gift.exception.NotFoundException;
import gift.repository.MemberRepository;
import gift.token.JwtTokenProvider;
import gift.util.BCryptEncryptor;
import gift.util.PasswordUtility;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public MemberDto createMember(NewMemberCommand newMemberCommand) {
        checkValidMemberUpdate(newMemberCommand.email(), null);

        String encodedPassword = BCryptEncryptor.encrypt(newMemberCommand.password());
        Member member = new Member(newMemberCommand.email(), encodedPassword,
                newMemberCommand.role());
        return MemberDto.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public String login(LoginMemberCommand command) {
        Optional<Member> optionalMember = memberRepository.findByEmail(command.email());
        if (optionalMember.isEmpty() || !BCryptEncryptor.matches(command.password(),
                optionalMember.get().getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtTokenProvider.createToken(optionalMember.get());
    }

    @Transactional(readOnly = true)
    public Page<MemberDto> getMemberList(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return memberPage.map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        Member member = findMemberById(id);
        return MemberDto.from(member);
    }

    @Transactional
    public MemberDto updateMember(UpdateMemberCommand updateMemberCommand) {
        Member member = findMemberById(updateMemberCommand.id());
        checkValidMemberUpdate(updateMemberCommand.email(), member.getId());
        String rawPassword = null;
        String encodedPassword = null;
        if (updateMemberCommand.resetPassword()) {
            rawPassword = PasswordUtility.generateRandomPassword();
            encodedPassword = BCryptEncryptor.encrypt(rawPassword);
        }
        member.applyPatch(updateMemberCommand.email(), encodedPassword, updateMemberCommand.role());
        return MemberDto.from(member, rawPassword);
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

    protected Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found"));
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
