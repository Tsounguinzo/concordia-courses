package courses.concordia.service;

public interface TokenBlacklistService {
    String BLACKLIST_PREFIX = "blacklist:";
    void blacklistToken(String token, long durationInSeconds);
    boolean isTokenBlacklisted(String token);
}
