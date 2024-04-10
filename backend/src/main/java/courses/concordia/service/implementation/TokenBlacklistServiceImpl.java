package courses.concordia.service.implementation;

import courses.concordia.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service implementation for handling token blacklisting operations.
 * This service provides functionalities to blacklist tokens and check if a token is blacklisted
 * by utilizing Redis as the storage mechanism.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Blacklists a token by adding it to a Redis store with a specified duration.
     * Once the duration expires, the token is automatically removed from the blacklist.
     *
     * @param token The token to be blacklisted.
     * @param durationInSeconds The duration in seconds for which the token should remain blacklisted.
     */
    @Override
    public void blacklistToken(String token, long durationInSeconds) {
        String key = BLACKLIST_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(durationInSeconds));
    }

    /**
     * Checks if a given token is currently blacklisted.
     *
     * @param token The token to check.
     * @return true if the token is blacklisted, false otherwise.
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
