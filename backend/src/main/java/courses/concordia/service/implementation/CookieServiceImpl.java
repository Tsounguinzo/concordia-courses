package courses.concordia.service.implementation;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CookieServiceImpl implements CookieService {
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;

    @Override
    public void clearTokenCookie(HttpServletResponse response, TokenType tokenType) {
        // clear jwt cookie
        Cookie cookie = new Cookie(tokenType == TokenType.accessToken ?
                jwtConfigProperties.getTokenName() : rtConfigProperties.getTokenName(), null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
    }

    @Override
    public void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {

        long maxAge = tokenType == TokenType.accessToken ?
                jwtConfigProperties.getExp() : rtConfigProperties.getExp();
        String cookieName = tokenType == TokenType.accessToken ?
                jwtConfigProperties.getTokenName() : rtConfigProperties.getTokenName();

        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
