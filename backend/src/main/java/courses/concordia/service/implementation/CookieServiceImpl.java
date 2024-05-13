package courses.concordia.service.implementation;

import courses.concordia.config.CookieConfigProperties;
import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class CookieServiceImpl implements CookieService {
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;
    private final CookieConfigProperties cookieConfigProperties;

    /**
     * Retrieves a token from the request cookies by its name.
     *
     * @param request   The incoming HTTP request.
     * @param tokenName The name of the token to retrieve.
     * @return The token value if present, or null otherwise.
     */
    @Override
    public String getTokenFromCookie(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Clears a specific token cookie by setting its max age to 0.
     *
     * @param response  The outgoing HTTP response.
     * @param tokenType The type of the token (ACCESS or REFRESH) to clear.
     */
    @Override
    public void clearTokenCookie(HttpServletResponse response, TokenType tokenType) {
        String cookieName = tokenType == TokenType.ACCESS_TOKEN ?
                jwtConfigProperties.getTokenName() : rtConfigProperties.getTokenName();

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(cookie.isHttpOnly());
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
    }

    /**
     * Adds a token as a secure, HttpOnly cookie to the response.
     *
     * @param response  The outgoing HTTP response.
     * @param token     The token value to set in the cookie.
     * @param tokenType The type of the token (ACCESS or REFRESH).
     */
    @Override
    public void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {

        long maxAge = tokenType == TokenType.ACCESS_TOKEN ?
                jwtConfigProperties.getExp() : rtConfigProperties.getExp();
        String cookieName = tokenType == TokenType.ACCESS_TOKEN ?
                jwtConfigProperties.getTokenName() : rtConfigProperties.getTokenName();

        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(cookieConfigProperties.isHttpOnly())
                .secure(cookieConfigProperties.isSecure())
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public void addTokenCookies(HttpServletResponse response, AuthenticationResponse res) {
        addTokenCookie(response, res.getToken(), TokenType.ACCESS_TOKEN);
        addTokenCookie(response, res.getRefreshToken(), TokenType.REFRESH_TOKEN);
    }

    /**
     * Clears all token cookies by setting their max age to 0.
     *
     * @param response The outgoing HTTP response.
     */
    @Override
    public void clearTokenCookies(HttpServletResponse response) {
        clearTokenCookie(response, TokenType.ACCESS_TOKEN);
        clearTokenCookie(response, TokenType.REFRESH_TOKEN);
    }
}
