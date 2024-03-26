package courses.concordia.security;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.service.JwtService;
import courses.concordia.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
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

import static courses.concordia.util.Misc.getTokenFromCookie;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserDetailsService userDetailsService;
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String jwtToken = getTokenFromCookie(request, jwtConfigProperties.getTokenName());
            final String refreshToken = getTokenFromCookie(request, rtConfigProperties.getTokenName());
            /*System.out.println(jwtToken);
            System.out.println(refreshToken);*/

            if (jwtToken != null && !tokenBlacklistService.isTokenBlacklisted(jwtToken)) {
                String username = jwtService.extractUsername(jwtToken);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (ExpiredJwtException eje) {
            log.warn("Expired JWT token: " + eje.getMessage());
            return;
        } catch (Exception ex) {
            log.error("JWT processing error: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
