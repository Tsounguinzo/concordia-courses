package courses.concordia.service.implementation.token;

import courses.concordia.service.TokenGenerator;

import java.security.SecureRandom;

public class AlphanumericTokenGenerator implements TokenGenerator {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a token consisting of a sequence of random characters. The characters
     * are selected from a predefined set and the length of the token is 36 characters.
     *
     * @return A String representing the generated token.
     */
    @Override
    public String generateToken() {
        StringBuilder sb = new StringBuilder(36);
        for (int i = 0; i < 36; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }
}
