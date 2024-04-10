package courses.concordia.service.implementation;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;
    private final Key signInKey;
    private final Key refreshKey;

    /**
     * Initializes the service with JWT and RT (Refresh Token) configuration properties.
     *
     * @param jwtConfigProperties Configuration properties for JWT.
     * @param rtConfigProperties  Configuration properties for Refresh Token.
     */
    public JwtServiceImpl(JwtConfigProperties jwtConfigProperties, RtConfigProperties rtConfigProperties) {
        this.jwtConfigProperties = jwtConfigProperties;
        this.signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigProperties.getSecret()));
        this.rtConfigProperties = rtConfigProperties;
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(rtConfigProperties.getSecret()));
    }

    /**
     * Extracts the username from a token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    @Override
    public String extractUsername(String token) {
        return extractUsername(token, TokenType.ACCESS_TOKEN);
    }

    /**
     * Extracts the username from a token of a specified type.
     *
     * @param token     The JWT token.
     * @param tokenType The type of the token (ACCESS or REFRESH).
     * @return The username extracted from the token.
     */
    @Override
    public String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getSubject, tokenType);
    }

    /**
     * Extracts a specific claim from a token.
     *
     * @param token         The JWT token.
     * @param claimsResolver A function to extract the desired claim.
     * @param tokenType     The type of the token (ACCESS or REFRESH).
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a token for a user with specified token type.
     *
     * @param userDetails UserDetails for the subject of the token.
     * @param tokenType   The type of the token (ACCESS or REFRESH).
     * @return A new JWT token.
     */
    public String generateToken(UserDetails userDetails, TokenType tokenType) {
        return generateToken(new HashMap<>(), userDetails, tokenType);
    }

    /**
     * Generates a token for a user with specified token type.
     *
     * @param claims      Claims to include in the token.
     * @param userDetails UserDetails for the subject of the token.
     * @param tokenType   The type of the token (ACCESS or REFRESH).
     * @return A new JWT token.
     */
    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails,
            TokenType tokenType
    ) {
        long expirationTimeLong = (tokenType == TokenType.ACCESS_TOKEN)
                ? jwtConfigProperties.getExp() * 1000 // Convert seconds to milliseconds
                : rtConfigProperties.getExp() * 1000;

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(tokenType == TokenType.ACCESS_TOKEN ? signInKey : refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a token by checking its expiration and comparing its subject with UserDetails.
     *
     * @param token       The JWT token to validate.
     * @param userDetails UserDetails to compare with token subject.
     * @param tokenType   The type of the token (ACCESS or REFRESH).
     * @return true if the token is valid; false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String username = extractUsername(token,tokenType);
        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token, tokenType);
    }

   /**
     * Extracts the expiration date from a token.
     *
     * @param token     The JWT token.
     * @param tokenType The type of the token (ACCESS or REFRESH).
     * @return The expiration date extracted from the token.
     */
    public Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getExpiration, tokenType);
    }


    /**
     * Checks if a token has expired.
     *
     * @param token     The JWT token to check.
     * @param tokenType The type of the token (ACCESS or REFRESH).
     * @return true if the token has expired; false otherwise.
     */
    public boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token,tokenType).before(new Date());
    }

    // Helper method to extract all claims from a token
    private Claims extractAllClaims(String token, TokenType tokenType) throws JwtException{
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(tokenType == TokenType.ACCESS_TOKEN ? signInKey : refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("Could not parse token: {}", e.getMessage());
            throw e;
        }
    }
}
