package courses.concordia.service;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.model.User;

public interface UserService {
    void signup(UserDto userDto);
    UserDto changeUsername(UserDto userDto);
    UserDto changePassword(UserDto userDto, String newPassword);
    AuthenticationResponse authenticate(LoginRequest loginRequest);
    Boolean verifyToken(String token);
    boolean isUserVerified(String username);
}
