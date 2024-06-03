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
    private static final String REVIEW_SUBMISSION_ENDPOINT = "/api/v1/reviews";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().endsWith(REVIEW_SUBMISSION_ENDPOINT)) {
            String clientIP = request.getRemoteAddr();
            long currentTime = System.currentTimeMillis();
            requestCounts.merge(clientIP, currentTime, (oldTime, newTime) -> {
                if (newTime - oldTime < TimeUnit.SECONDS.toMillis(10)) { // Limit to 1 request per 10 seconds
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return oldTime;
                }
                return newTime;
            });
        }
        filterChain.doFilter(request, response);
    }
}
