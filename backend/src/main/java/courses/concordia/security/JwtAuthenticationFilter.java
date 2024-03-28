package courses.concordia.security;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.service.CookieService;
import courses.concordia.service.JwtService;
import courses.concordia.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserDetailsService userDetailsService;
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = cookieService.getTokenFromCookie(request, jwtConfigProperties.getTokenName());
            String refreshToken = cookieService.getTokenFromCookie(request, rtConfigProperties.getTokenName());
            UserDetails userDetails = null;

            if (jwtToken != null && !tokenBlacklistService.isTokenBlacklisted(jwtToken)) {
                userDetails = authenticateUsingJwtToken(jwtToken, request);
            }

            // If JWT authentication failed but refresh token is valid and not blacklisted
            if (userDetails == null && refreshToken != null && !tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
                userDetails = authenticateUsingRefreshToken(refreshToken, request, response);
            }

            // Continue filter chain if authentication succeeds
            if (userDetails != null) {
                setAuthenticationContext(userDetails, request);
            }
        } catch (ExpiredJwtException eje) {
            log.warn("Expired JWT token: {}", eje.getMessage());
        } catch (Exception ex) {
            log.error("JWT processing error: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails authenticateUsingJwtToken(String jwtToken, HttpServletRequest request) {
        try {
            String username = jwtService.extractUsername(jwtToken, TokenType.ACCESS_TOKEN);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails, TokenType.ACCESS_TOKEN)) {
                    setAuthenticationContext(userDetails, request);
                    return userDetails;
                }
            }
        } catch (JwtException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
        }
        return null;
    }

    private UserDetails authenticateUsingRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(refreshToken, userDetails, TokenType.REFRESH_TOKEN)) {
                    log.debug("Generating new access token for user: {}", userDetails.getUsername());
                    String newJwtToken = jwtService.generateToken(userDetails, TokenType.ACCESS_TOKEN);
                    cookieService.addTokenCookie(response, newJwtToken, TokenType.ACCESS_TOKEN);
                    setAuthenticationContext(userDetails, request);
                    return userDetails;
                }
            }
        } catch (JwtException e) {
            log.info("Failed to process refresh token: {}", e.getMessage());
        }
        return null;
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
