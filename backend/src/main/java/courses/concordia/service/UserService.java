package courses.concordia.service;

import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.model.User;

public interface UserService {
    UserDto signup(SignupRequest signupRequest);
    UserDto changeUsername(UserDto userDto);
    UserDto changePassword(UserDto userDto, String newPassword);
    UserDto getUserFromSession();
}
