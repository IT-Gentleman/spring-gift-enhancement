package gift.service;

import gift.dto.UpdateMemberResponse;
import gift.entity.Member;
import gift.entity.Role;
import gift.exception.ConflictException;
import gift.exception.InvalidCredentialsException;
import gift.repository.MemberRepository;
import gift.token.JwtTokenProvider;
import gift.util.BCryptEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private BCryptEncryptor bCryptEncryptor;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberService memberService;

    @Nested
    @DisplayName("Member createMember() - 회원 생성 테스트")
    class CreateMemberTests {

        @Test
        @DisplayName("정상적인 회원 데이터 삽입")
        void 정상적인_회원_삽입_시_정상반환() {
            String email = "new@email.com";
            String rawPassword = "newpassword123456789";
            String hashedPassword = "hashedPassword";

            Member expectedMember = new Member(1L, email, hashedPassword, Role.ROLE_USER);

            when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

            try (MockedStatic<BCryptEncryptor> encryptor = mockStatic(BCryptEncryptor.class)) {

                encryptor.when(() -> BCryptEncryptor.encrypt(rawPassword)).thenReturn(hashedPassword);
                when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

                Member resolve = memberService.createMember(email, rawPassword);

                assertAll(
                        () -> assertThat(resolve.getEmail()).isEqualTo(email),
                        () -> assertThat(resolve.getPassword()).isEqualTo(hashedPassword),
                        () -> assertThat(resolve.getId()).isEqualTo(1L),
                        () -> assertThat(resolve.getRole()).isEqualTo(Role.ROLE_USER)
                );
            }
        }

        @Test
        @DisplayName("이미 존재하는 이메일로 회원 생성 시 예외 발생")
        void 이미_존재하는_이메일로_회원_생성_시_예외발생() {
            String email = "existing@email.com";
            String rawPassword = "password123456789";

            when(memberRepository.findByEmail(email)).thenReturn(Optional.of(new Member(100L, email, "hashedPassword", null)));

            assertThrows(ConflictException.class, () -> memberService.createMember(email, rawPassword));
        }
    }

    @Nested
    @DisplayName("Member login() - 로그인 테스트")
    class LoginTests {

        @Test
        @DisplayName("정상적인 로그인 시 토큰 반환")
        void 정상적인_로그인_시_토큰반환() {
            String email = "registered@email.com";
            String rawPassword = "password123456789";
            String hashedPassword = "hashedPassword";
            Member existingMember = new Member(1L, email, hashedPassword, Role.ROLE_USER);

            when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

            try (MockedStatic<BCryptEncryptor> encryptor = mockStatic(BCryptEncryptor.class)) {

                encryptor.when(() -> BCryptEncryptor.matches(rawPassword, hashedPassword)).thenReturn(true);
                when(jwtTokenProvider.createToken(existingMember)).thenReturn("validToken");

                String token = memberService.login(email, rawPassword);

                assertThat(token).isEqualTo("validToken");
            }
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
        void 존재하지_않는_이메일로_로그인_시_예외발생() {
            String email = "not.registered@email.com";
            String rawPassword = "password123456789";
            String hashedPassword = "hashedPassword";

            when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(InvalidCredentialsException.class, () -> memberService.login(email, rawPassword));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 예외 발생")
        void 잘못된_비밀번호로_로그인_시_예외발생() {
            String email = "registered@email.com";
            String rawPassword = "wrongPassword123456789";
            String hashedPassword = "hashedPassword";

            Member existingMember = new Member(1L, email, hashedPassword, Role.ROLE_USER);

            when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

            try (MockedStatic<BCryptEncryptor> encryptor = mockStatic(BCryptEncryptor.class)) {

                encryptor.when(() -> BCryptEncryptor.matches(rawPassword, hashedPassword)).thenReturn(false);

                assertThrows(InvalidCredentialsException.class, () -> memberService.login(email, rawPassword));
            }
        }
    }

    @Nested
    @DisplayName("Member updateSelectivelyMember() - 회원 정보 수정 테스트")
    class UpdateSelectivelyMemberTests {

        @Test
        @DisplayName("정상적인 회원 정보 수정 - 비밀번호 유지")
        void 정상적인_회원_정보_수정_비밀번호_유지() {
            Long id = 1L;
            String email = "existing@email.com";
            String newEmail = "new@email.com";
            String hashedPassword = "hashedPassword";
            Role role = Role.ROLE_USER;
            Role newRole = Role.ROLE_ADMIN;

            Member existingMember = new Member(id, email, hashedPassword, role);
            when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
            when(memberRepository.findByEmail(newEmail)).thenReturn(Optional.empty());

            UpdateMemberResponse result = memberService.updateSelectivelyMember(id, newEmail, false, newRole);

            assertAll(
                    () -> assertThat(result.member().getId()).isEqualTo(id),
                    () -> assertThat(result.member().getEmail()).isEqualTo(newEmail),
                    () -> assertThat(result.member().getPassword()).isEqualTo(hashedPassword),
                    () -> assertThat(result.member().getRole()).isEqualTo(newRole),
                    () -> assertThat(result.temporalPassword()).isNull()
            );
        }

        @Test
        @DisplayName("정상적인 회원 정보 수정 - 비밀번호 변경")
        void 정상적인_회원_정보_수정_비밀번호_변경() {
            Long id = 1L;
            String email = "existing@email.com";
            String newEmail = "new@email.com";
            String hashedPassword = "hashedPassword";
            String newPassword = "newPassword";
            Role role = Role.ROLE_USER;
            Role newRole = Role.ROLE_ADMIN;

            Member existingMember = new Member(id, email, hashedPassword, role);
            when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
            when(memberRepository.findByEmail(newEmail)).thenReturn(Optional.empty());

            try (MockedStatic<BCryptEncryptor> encryptor = mockStatic(BCryptEncryptor.class)) {
                encryptor.when(() -> BCryptEncryptor.encrypt(any())).thenReturn(newPassword);

                UpdateMemberResponse result = memberService.updateSelectivelyMember(id, newEmail, true, newRole);

                assertAll(
                        () -> assertThat(result.member().getId()).isEqualTo(id),
                        () -> assertThat(result.member().getEmail()).isEqualTo(newEmail),
                        () -> assertThat(result.member().getPassword()).isEqualTo(newPassword),
                        () -> assertThat(result.member().getRole()).isEqualTo(newRole),
                        () -> assertThat(result.temporalPassword()).isNotNull()
                );
            }
        }

        // 이메일 유지하는 정상적인 회원 정보 수정
        // 이메일 중복인 비정상적인 회원 정보 수정
    }

    // 단순 CRUD 테스트 생략

    @Nested
    @DisplayName("Member getAuthenticationFromToken() - 토큰으로 인증 정보 조회 테스트")
    class GetAuthenticationFromTokenTests {

        @Test
        @DisplayName("유효한 토큰으로 인증 정보 조회")
        void 유효한_토큰으로_인증정보조회() {
            String username = "email@email.com";
            String token = "validToken";
            Member member = new Member(1L, "email", "hashedPassword", Role.ROLE_USER);
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.getUsername(token)).thenReturn(username);
            when(memberRepository.findByEmail(username)).thenReturn(Optional.of(member));
            assertThat(memberService.getAuthenticationFromToken(token)).isNotNull();
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 인증 정보 조회 시 예외 발생")
        void 유효하지_않은_토큰으로_인증정보조회시_예외발생() {
            String token = "validToken";
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);
            assertThrows(ResponseStatusException.class, () -> memberService.getAuthenticationFromToken(token));
        }

        @Test
        @DisplayName("유효한 토큰이지만 회원 정보가 없는 경우 예외 발생")
        void 유효한_토큰이지만_회원정보가_없는_경우_예외발생() {
            String username = "deletedMember@email.com";
            String token = "validToken";
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.getUsername(token)).thenReturn(username);
            when(memberRepository.findByEmail(username)).thenReturn(Optional.empty());
            assertThrows(ResponseStatusException.class, () -> memberService.getAuthenticationFromToken(token));
        }
    }
}