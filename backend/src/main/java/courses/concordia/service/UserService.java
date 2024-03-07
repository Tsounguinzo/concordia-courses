package courses.concordia.service;

import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.model.User;

public interface UserService {
    AuthenticationResponse signup(UserDto userDto);
    UserDto findUserByUsername(String username);

    UserDto updateProfile(UserDto userDto);
    UserDto changePassword(UserDto userDto, String newPassword);
    UserDto getUserFromSession();
}
