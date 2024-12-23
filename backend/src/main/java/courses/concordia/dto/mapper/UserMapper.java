package courses.concordia.dto.mapper;

import courses.concordia.dto.model.user.UserDto;
import courses.concordia.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setVerified(user.isVerified());
    }
}
