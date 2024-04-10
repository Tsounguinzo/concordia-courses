package courses.concordia.service.implementation.token;

import courses.concordia.service.TokenGenerator;
import java.util.Random;

public class NumericTokenGenerator implements TokenGenerator {

    /**
     * Generates a numeric token of a fixed length of 6 digits. This method uses a
     * Random object to generate each digit of the token.
     *
     * @return A String representing the 6-digit numeric token.
     */
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
