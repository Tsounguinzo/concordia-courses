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


    public JwtServiceImpl(JwtConfigProperties jwtConfigProperties, RtConfigProperties rtConfigProperties) {
        this.jwtConfigProperties = jwtConfigProperties;
        this.signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigProperties.getSecret()));
        this.rtConfigProperties = rtConfigProperties;
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(rtConfigProperties.getSecret()));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, TokenType tokenType) {
        return generateToken(new HashMap<>(), userDetails, tokenType);
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails,
            TokenType tokenType
    ) {
        long expirationTimeLong = 0;
        if(tokenType == TokenType.accessToken) {
            expirationTimeLong = jwtConfigProperties.getExp() * 1000; // Convert seconds to milliseconds
        }if(tokenType == TokenType.refreshToken){
            expirationTimeLong = rtConfigProperties.getExp() * 1000;
        }
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(tokenType == TokenType.accessToken ? signInKey : refreshKey,
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) throws JwtException{
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signInKey)
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
