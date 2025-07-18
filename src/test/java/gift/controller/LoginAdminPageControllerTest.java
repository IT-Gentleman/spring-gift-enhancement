package gift.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = LoginAdminPageController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class})
class LoginAdminPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void 로그인_페이지_요청_시_로그인페이지() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(view().name("admin/login-form"))
                .andExpect(model().attributeExists("member"));
    }

    @Test
    void 유효한_로그인_요청_시_리다이렉션() throws Exception {
        String email = "hello@world.com";
        String password = "password123456789";
        String expectedToken = "dummy-jwt-token";
        when(memberService.login(email, password)).thenReturn(expectedToken);

        mockMvc.perform(post("/admin/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(redirectedUrl("/admin"))
                .andExpect(cookie().value("token", expectedToken));
    }

    @Test
    void 로그아웃_요청_시_리다이렉션() throws Exception {
        mockMvc.perform(
                        post("/admin/logout")
                                .cookie(new Cookie("token", "dummy-token")))
                .andExpect(cookie().maxAge("token", 0)
                );
    }
}