package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Role;
import gift.entity.WishItem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    @DisplayName("WishItem save() - 위시 아이템 생성 테스트")
    class SaveTests {

        @Test
        @DisplayName("정상적인 위시 아이템 데이터 삽입")
        void 정상적인_위시_아이템_삽입_시_정상반환() {
            WishItem wishItem = new WishItem(
                    null,
                    existingMember,
                    existingProduct,
                    LocalDateTime.now()
            );
            WishItem savedWishItem = wishRepository.save(wishItem);
            assertAll(
                () -> assertThat(savedWishItem.getId()).isNotNull(),
                () -> assertThat(savedWishItem.getMember().getIdentifyNumber()).isEqualTo(existingMember.getIdentifyNumber()),
                () -> assertThat(savedWishItem.getProduct().getId()).isEqualTo(existingProduct.getId())
            );
        }

        @Test
        @DisplayName("null 값이 포함된 위시 아이템 데이터 삽입 시 예외 발생")
        void 널_값이_포함된_위시_아이템_삽입_시_예외_발생() {
            // id는 항상 null인 상태로 삽입되어야 함

            WishItem allNullWishItem = new WishItem(null, null, null, null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(allNullWishItem));

            WishItem memberNullWishItem = new WishItem(null, null, existingProduct, LocalDateTime.now());
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(memberNullWishItem));

            WishItem productNullWishItem = new WishItem(null, existingMember, null, LocalDateTime.now());
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(productNullWishItem));
        }

        @Test
        @DisplayName("FK 제약 조건 위반 시 예외 발생")
        void 외래키_제약_조건_위반_시_예외_발생() {
            // 존재하지 않는 member / product로 삽입 시도
            Member nonExistentMember = new Member(999L, null, null, null);
            Product nonExistentProduct = new Product(999L, null, null, null, null, null);
            WishItem wishItem = new WishItem(
                  null,
                    nonExistentMember,
                    nonExistentProduct,
                    LocalDateTime.now()
            );
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> wishRepository.save(wishItem));
        }
    }

    @Nested
    @DisplayName("List<WishItem> findAllByMemberIdentifyNumber(Long memberId) - 위시 리스트 조회 테스트")
    class FindAllByMemberIdentifyNumberTests {

        @Test
        @DisplayName("정상적인 memberId로 위시 리스트 조회")
        void 정상적인_memberId로_위시_리스트_조회() {
            WishItem wishItem = new WishItem(
                    null,
                    existingMember,
                    existingProduct,
                    LocalDateTime.now()
            );
            wishRepository.save(wishItem);

            List<WishItem> wishItems = wishRepository.findAllByMemberIdentifyNumber(existingMember.getIdentifyNumber());
            assertAll(
                () -> assertThat(wishItems).hasSize(1),
                () -> assertThat(wishItems.get(0).getId()).isNotNull(),
                () -> assertThat(wishItems.get(0).getMember().getIdentifyNumber()).isEqualTo(existingMember.getIdentifyNumber()),
                () -> assertThat(wishItems.get(0).getProduct().getId()).isEqualTo(existingProduct.getId())
            );
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 위시 아이템 조회 시 빈 리스트 반환")
        void 존재하지_않는_memberId로_위시_아이템_조회_시_빈_리스트_반환() {
            List<WishItem> wishItems = wishRepository.findAllByMemberIdentifyNumber(999L);
            assertThat(wishItems).hasSize(0);
        }
    }

    @Nested
    @DisplayName("Integer removeByMemberIdentifyNumberAndId(Long memberId, Long wishId) - 위시 아이템 삭제 테스트")
    class RemoveByMemberIdentifyNumberAndProductIdTests {

        @Test
        @DisplayName("정상적인 memberId와 wishId로 위시 아이템 삭제")
        void 정상적인_memberId와_productId로_위시_아이템_삭제_시_1반환() {
            WishItem wishItem = new WishItem(
                    null,
                    existingMember,
                    existingProduct,
                    LocalDateTime.now()
            );
            wishItem = wishRepository.save(wishItem);

            assertThat(wishRepository.removeByMemberIdentifyNumberAndId(existingMember.getIdentifyNumber(), wishItem.getId())).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하는 memberId에 대해 존재하지 않는 productId로 위시 아이템 삭제 시 false 반환")
        void 존재하는_memberId에_대해_존재하지_않는_productId로_위시_아이템_삭제_시_0반환() {
            // 본인 소유가 아닌 wishItem 삭제 시도
            WishItem wishItem = new WishItem(
                    null,
                    existingMember,
                    existingProduct,
                    LocalDateTime.now()
            );
            wishItem = wishRepository.save(wishItem);

            assertThat(wishRepository.removeByMemberIdentifyNumberAndId(999L, wishItem.getId())).isEqualTo(0);

        }
    }
}