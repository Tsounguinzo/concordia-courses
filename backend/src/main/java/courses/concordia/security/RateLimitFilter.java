package courses.concordia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Long> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIP = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();
        requestCounts.merge(clientIP, currentTime, (oldTime, newTime) -> {
            if (newTime - oldTime < TimeUnit.SECONDS.toMillis(10)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return oldTime;
            }
            return newTime;
        });

        filterChain.doFilter(request, response);
    }
}
