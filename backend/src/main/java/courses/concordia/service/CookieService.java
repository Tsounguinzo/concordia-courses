package courses.concordia.service;

import courses.concordia.config.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public interface CookieService {
    void clearTokenCookie(HttpServletResponse response, TokenType tokenType);
    void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType);
}
