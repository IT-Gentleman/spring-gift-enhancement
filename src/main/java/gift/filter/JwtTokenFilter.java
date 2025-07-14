package gift.filter;

import gift.token.JwtTokenProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Optional;

// 1. extracting JWT Token from the request and validating it
public class JwtTokenFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String token = Optional.ofNullable(getTokenFromCookies(httpRequest))
                .orElse(getTokenFromAuthorizationHeader(httpRequest));
        if (token != null && jwtTokenProvider.validateToken(token)) {
            httpRequest.setAttribute("username", jwtTokenProvider.getUsername(token));
            httpRequest.setAttribute("role", jwtTokenProvider.getRole(token));
        } else {
            httpRequest.setAttribute("username", null);
            httpRequest.setAttribute("role", null);
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, "token"))
                .map(cookie -> cookie.getValue())
                .orElse(null);
    }

    private String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
