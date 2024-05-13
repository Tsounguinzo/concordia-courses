package courses.concordia.service;

import courses.concordia.config.TokenType;
import courses.concordia.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    String getTokenFromCookie(HttpServletRequest request, String tokenName);
    void clearTokenCookie(HttpServletResponse response, TokenType tokenType);
    void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType);
    void addTokenCookies(HttpServletResponse response, AuthenticationResponse res);
    void clearTokenCookies(HttpServletResponse response);
}
