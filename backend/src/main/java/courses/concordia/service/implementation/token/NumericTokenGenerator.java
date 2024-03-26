package courses.concordia.service.implementation.token;

import courses.concordia.service.TokenGenerator;
import java.util.Random;

public class NumericTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken() {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
