package courses.concordia.service;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;

public interface UserService {
    AuthenticationResponse signup(UserDto userDto);
    UserDto changeUsername(String newUsername, String originalUsername);
    UserDto changePassword(UserDto userDto, String newPassword);
    AuthenticationResponse authenticate(LoginRequest loginRequest);
    boolean verifyToken(String token);
    boolean isUserVerified(String username);
    void resendToken(String token);
}
