package courses.concordia.service;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.model.Token;
import courses.concordia.model.User;

public interface UserService {
    AuthenticationResponse signup(UserDto userDto);
    UserDto changeUsername(String newUsername, String originalUsername);
    void changePassword(Token token, String newPassword);
    AuthenticationResponse authenticate(LoginRequest loginRequest);
    boolean verifyToken(String token);
    User getAuthenticatedUser();
    void resendToken(String username);
    boolean checkIfUserExist(String username);
    String verifyPasswordResetToken(String token);
    void forgotPassword(String username);
}
