package courses.concordia.service.implementation.token;

import courses.concordia.service.TokenGenerator;

import java.util.UUID;

public class UUIDTokenGenerator implements TokenGenerator {

    /**
     * Generates a unique token using Java's built-in UUID generator. This method
     * leverages the UUID.randomUUID() method to generate a unique identifier
     * which is returned as a string.
     *
     * @return A String representing the UUID token.
     */
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
