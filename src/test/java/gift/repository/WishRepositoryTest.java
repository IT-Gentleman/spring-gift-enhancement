package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Role;
import gift.entity.Wish;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member existingMember;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        existingMember = memberRepository.save(
            new Member(
                null,
                "existing@kakao.com",
                "existingEncryptedPassword",
                Role.ROLE_USER
            )
        );
        existingProduct = productRepository.save(
            new Product(
                null,
                "existing product",
                5000,
                "http://image.com/existing.png",
                true,
                false
            )
        );
    }

    @Nested
    @DisplayName("Wish save() - 위시 아이템 생성 테스트")
    class SaveTests {

        @Test
        @DisplayName("정상적인 위시 아이템 데이터 삽입")
        void 정상적인_위시_아이템_삽입_시_정상반환() {
            Wish wish = new Wish(
                    existingMember,
                    existingProduct
            );
            Wish savedWish = wishRepository.save(wish);
            assertAll(
                () -> assertThat(savedWish.getId()).isNotNull(),
                () -> assertThat(savedWish.getMember().getId()).isEqualTo(existingMember.getId()),
                () -> assertThat(savedWish.getProduct().getId()).isEqualTo(existingProduct.getId())
            );
        }

        @Test
        @DisplayName("null 값이 포함된 위시 아이템 데이터 삽입 시 예외 발생")
        void 널_값이_포함된_위시_아이템_삽입_시_예외_발생() {

            Wish allNullWish = new Wish(null, null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(allNullWish));

            Wish memberNullWish = new Wish(null, existingProduct);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(memberNullWish));

            Wish productNullWish = new Wish(existingMember, null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(productNullWish));
        }

        @Test
        @DisplayName("FK 제약 조건 위반 시 예외 발생")
        void 외래키_제약_조건_위반_시_예외_발생() {
            // 존재하지 않는 member / product로 삽입 시도
            Member nonExistentMember = new Member(999L, null, null, null);
            Product nonExistentProduct = new Product(999L, null, null, null, null, null);
            Wish wish = new Wish(
                    nonExistentMember,
                    nonExistentProduct
            );
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(wish));
        }
    }

    @Nested
    @DisplayName("List<Wish> findAllByMemberId(Long memberId) - 위시 리스트 조회 테스트")
    class FindAllByMemberIdentifyNumberTests {

        @Test
        @DisplayName("정상적인 memberId로 위시 리스트 조회")
        void 정상적인_memberId로_위시_리스트_조회() {
            Wish wish = new Wish(
                    existingMember,
                    existingProduct
            );
            wishRepository.save(wish);

            List<Wish> wishes = wishRepository.findAllByMemberId(existingMember.getId());
            assertAll(
                () -> assertThat(wishes).hasSize(1),
                () -> assertThat(wishes.get(0).getId()).isNotNull(),
                () -> assertThat(wishes.get(0).getMember().getId()).isEqualTo(existingMember.getId()),
                () -> assertThat(wishes.get(0).getProduct().getId()).isEqualTo(existingProduct.getId())
            );
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 위시 아이템 조회 시 빈 리스트 반환")
        void 존재하지_않는_memberId로_위시_아이템_조회_시_빈_리스트_반환() {
            List<Wish> wishes = wishRepository.findAllByMemberId(999L);
            assertThat(wishes).hasSize(0);
        }
    }

    @Nested
    @DisplayName("Integer deleteByIdAndMemberId(Long memberId, Long wishId) - 위시 아이템 삭제 테스트")
    class RemoveByMemberIdentifyNumberAndProductIdTests {

        @Test
        @DisplayName("정상적인 memberId와 wishId로 위시 아이템 삭제 시 삭제")
        void 정상적인_memberId와_productId로_위시_아이템_삭제_시_삭제() {
            Wish wish = new Wish(
                    existingMember,
                    existingProduct
            );
            wish = wishRepository.save(wish);

            assertThat(wishRepository.findById(wish.getId())).isPresent();
            wishRepository.deleteByIdAndMemberId(wish.getId(), existingMember.getId());
            assertThat(wishRepository.findById(wish.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하는 memberId에 대해 존재하지 않는 productId로 위시 아이템 삭제 시 미삭제")
        void 존재하는_memberId에_대해_존재하지_않는_productId로_위시_아이템_삭제_시_미삭제() {
            // 본인 소유가 아닌 wish 삭제 시도
            Wish wish = new Wish(
                    existingMember,
                    existingProduct
            );
            wish = wishRepository.save(wish);

            assertThat(wishRepository.findById(wish.getId())).isPresent();
            wishRepository.deleteByIdAndMemberId(wish.getId(), 999L);
            assertThat(wishRepository.findById(wish.getId())).isPresent();
        }
    }
}