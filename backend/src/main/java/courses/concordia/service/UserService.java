package courses.concordia.service;

import courses.concordia.controller.request.LoginRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.model.Token;
import courses.concordia.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    AuthenticationResponse signup(UserDto userDto);
    UserDto changeUsername(String newUsername, String originalUsername);
    void changePassword(Token token, String newPassword);
    boolean verifyToken(String token);
    User getAuthenticatedUser();
    void resendToken(String username);
    String verifyPasswordResetToken(String token);
    void forgotPassword(String username);
    void signOut(HttpServletRequest request, HttpServletResponse response);
    void signIn(LoginRequest loginRequest, HttpServletResponse response);
}
