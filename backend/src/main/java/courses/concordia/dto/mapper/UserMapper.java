package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.model.Course;
import courses.concordia.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getUsername(),
                user.getEmail(),
                user.getPassword());
    }
}
