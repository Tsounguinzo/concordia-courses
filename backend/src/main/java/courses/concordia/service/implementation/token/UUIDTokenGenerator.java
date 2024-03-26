package courses.concordia.service.implementation.token;

import courses.concordia.service.TokenGenerator;

import java.util.UUID;

public class UUIDTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
