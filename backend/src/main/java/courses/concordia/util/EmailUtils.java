package courses.concordia.util;

public class EmailUtils {

    public static String getEmailMessage(String name, String token) {
        return "Hello " + name + ",\n\nYour new account has been created.  \n\n Verification code: " +
                token + "\n\nThis link will be expired in 5 minutes";
    }

    /*public static String getVerificationUrl(String host, String token) {
        return host + "/api/v1/auth/authorized?token=" + token;
    }*/
}
