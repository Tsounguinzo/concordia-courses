package courses.concordia.dto.mapper;

import courses.concordia.dto.model.user.UserDto;
import courses.concordia.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getVerified());
    }
}
