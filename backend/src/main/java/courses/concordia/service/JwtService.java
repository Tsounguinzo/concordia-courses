package courses.concordia.service;

import courses.concordia.config.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token, TokenType tokenType);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType);
    String generateToken(UserDetails userDetails, TokenType tokenType);
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, TokenType tokenType);
    boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);
    boolean isTokenExpired(String token);
    String verifyRefreshToken(UserDetails userDetails, String refreshToken);
    Date extractExpiration(String token, TokenType tokenType);
}
