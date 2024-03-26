package courses.concordia.service.implementation;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;

    public CookieServiceImpl(JwtConfigProperties jwtConfigProperties, RtConfigProperties rtConfigProperties) {
        this.jwtConfigProperties = jwtConfigProperties;

        this.rtConfigProperties = rtConfigProperties;
    }

    @Override
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtConfigProperties.getTokenName(), null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
    }

    @Override
    public void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {
        ResponseCookie cookie = ResponseCookie.from(
                        tokenType == TokenType.accessToken ?
                                jwtConfigProperties.getTokenName() : rtConfigProperties.getTokenName(), token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(tokenType == TokenType.accessToken ?
                        jwtConfigProperties.getExp() : rtConfigProperties.getExp())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
