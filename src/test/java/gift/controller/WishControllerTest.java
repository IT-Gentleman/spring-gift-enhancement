package gift.controller;

import gift.dto.AddWishItemRequest;
import gift.dto.WishItemResponse;
import gift.entity.Member;
import gift.entity.Role;
import gift.entity.WishItem;
import gift.service.MemberService;
import gift.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/clear_wish_table.sql")
class WishControllerTest {

    private final String baseUrl = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private MemberService memberService;

    private RestClient restClient;

    private String userToken;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create();

        Member user = new Member(0L, "user@examle.com", "userpassword123456789", Role.ROLE_USER);
        userToken = jwtTokenProvider.createToken(user);

        when(memberService.getMemberFromToken(userToken)).thenReturn(user);
    }

    @Nested
    @DisplayName("GET /api/wishes - 위시리스트 조회 테스트")
    class GetWishList {
        String url = baseUrl + port + "/api/wishes";

        @Test
        @DisplayName("GET /api/wishes - 위시리스트 조회 시 200 OK")
        void 위시리스트_조회_시_200_OK() {
            ResponseEntity<List<WishItem>> response = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + userToken)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<WishItem>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("GET /api/wishes - 비로그인 상태로 위시리스트 조회 시 401 UNAUTHORIZED")
        void 비로그인_상태로_위시리스트_조회_시_401_UNAUTHORIZED() {
            assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
                    .isThrownBy(() -> restClient.get()
                            .uri(url)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<List<WishItem>>() {}));
        }
    }

    @Nested
    @DisplayName("POST /api/wishes - 위시리스트 아이템 추가 테스트")
    class PostWish {
        String url = baseUrl + port + "/api/wishes";

        @Test
        @DisplayName("POST /api/wishes - 유효한 아이템 추가 시 201 CREATED")
        void 유효한_아이템_추가_시_201_CREATED() {
            AddWishItemRequest request = new AddWishItemRequest(1L);
            ResponseEntity<WishItemResponse> response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + userToken)
                    .body(request)
                    .retrieve()
                    .toEntity(WishItemResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("POST /api/wishes - 유효하지 않은 아이템 추가 시 404 NOT_FOUND")
        void 유효하지_않은_아이템_추가_시_404_NOT_FOUND() {
            AddWishItemRequest request = new AddWishItemRequest(0L);
            assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
                    .isThrownBy(() -> restClient.post()
                            .uri(url)
                            .header("Authorization", "Bearer " + userToken)
                            .body(request)
                            .retrieve()
                            .toEntity(WishItemResponse.class));
        }

        @Test
        @DisplayName("POST /api/wishes - 이미 존재하는 아이템 추가 시 409 CONFLICT")
        void 이미_존재하는_아이템_추가_시_409_CONFLICT() {
            AddWishItemRequest request = new AddWishItemRequest(1L);
            // 첫 번째 추가
            restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + userToken)
                    .body(request)
                    .retrieve()
                    .toEntity(WishItemResponse.class);

            // 두 번째 추가 시도
            assertThatExceptionOfType(HttpClientErrorException.Conflict.class)
                    .isThrownBy(() -> restClient.post()
                            .uri(url)
                            .header("Authorization", "Bearer " + userToken)
                            .body(request)
                            .retrieve()
                            .toEntity(WishItemResponse.class));
        }

        @Test
        @DisplayName("POST /api/wishes - 비로그인 상태로 위시리스트 추가 시 401 UNAUTHORIZED")
        void 비로그인_상태로_위시리스트_추가_시_401_UNAUTHORIZED() {
            assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
                    .isThrownBy(() -> restClient.get()
                            .uri(url)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<List<WishItem>>() {}));
        }
    }

    @Nested
    @Sql({"/clear_member_table.sql","/clear_wish_table.sql","/insert_member_item.sql", "/insert_wish_item.sql"})
    @DisplayName("DELETE /api/wishes/{wishItemId} - 위시리스트 아이템 삭제 테스트")
    class DeleteWish {
        String url = baseUrl + port + "/api/wishes";

        @Test
        @DisplayName("DELETE /api/wishes/{wishItemId} - 유효한 아이템 삭제 시 204 NO_CONTENT")
        void 유효한_아이템_삭제_시_204_NO_CONTENT() {
            Long wishItemId = 100L;

            ResponseEntity<Void> response = restClient.delete()
                    .uri(url + "/" + wishItemId)
                    .header("Authorization", "Bearer " + userToken)
                    .retrieve()
                    .toEntity(Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("DELETE /api/wishes/{wishItemId} - 존재하지 않는 아이템 삭제 시 404 NOT_FOUND")
        void 존재하지_않는_아이템_삭제_시_404_NOT_FOUND() {
            // 존재하지 않는 wishItemId
            Long invalidWishItemId = 150L;

            assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
                    .isThrownBy(() -> restClient.delete()
                            .uri(url + "/" + invalidWishItemId)
                            .header("Authorization", "Bearer " + userToken)
                            .retrieve()
                            .toEntity(Void.class));
        }

        @Test
        @DisplayName("DELETE /api/wishes/{wishItemId} - 다른 사용자의 아이템 삭제 시 404 NOT_FOUND")
        void 다른_사용자의_아이템_삭제_시_404_NOT_FOUND() {
            // 다른 사용자가 소유한 wishItemId
            Long otherUserWishItemId = 200L;

            assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
                    .isThrownBy(() -> restClient.delete()
                            .uri(url + "/" + otherUserWishItemId)
                            .header("Authorization", "Bearer " + userToken)
                            .retrieve()
                            .toEntity(Void.class));
        }

        @Test
        @DisplayName("DELETE /api/wishes/{wishItemId} - 비로그인 상태로 위시리스트 추가 시 401 UNAUTHORIZED")
        void 비로그인_상태로_아이템_삭제_시_401_UNAUTHORIZED() {
            assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
                    .isThrownBy(() -> restClient.get()
                            .uri(url)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<List<WishItem>>() {}));
        }
    }
}