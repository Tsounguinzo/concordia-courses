package courses.concordia.service;

import courses.concordia.dto.model.user.UserDto;
import courses.concordia.model.User;

public interface UserService {
    UserDto signup(UserDto userDto);
    UserDto findUserByUsername(String username);

    UserDto updateProfile(UserDto userDto);
    UserDto changePassword(UserDto userDto, String newPassword);
}
