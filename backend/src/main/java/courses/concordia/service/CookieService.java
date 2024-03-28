package courses.concordia.service;

import courses.concordia.config.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    String getTokenFromCookie(HttpServletRequest request, String tokenName);
    void clearTokenCookie(HttpServletResponse response, TokenType tokenType);
    void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType);
}
