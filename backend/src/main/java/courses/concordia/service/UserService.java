package courses.concordia.service;

import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;

public interface UserService {
    AuthenticationResponse signup(UserDto userDto);
    UserDto changeUsername(UserDto userDto);
    UserDto changePassword(UserDto userDto, String newPassword);
}
