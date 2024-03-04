package courses.concordia.service.implementation;

import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.User;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static courses.concordia.exception.EntityType.USER;
import static courses.concordia.exception.ExceptionType.DUPLICATE_ENTITY;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void signup(SignupRequest signupRequest) {
        Optional<User> user1 = userRepository.findByUsername(signupRequest.getUsername());
        Optional<User> user2 = userRepository.findByEmail(signupRequest.getEmail());
        if (user1.isEmpty() && user2.isEmpty()) {
            User user = new User()
                    .setUsername(signupRequest.getUsername())
                            .setEmail(signupRequest.getEmail())
                                    .setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));

            emailService.sendSimpleEmail(signupRequest.getEmail(), "Welcome", "This is a welcome email for your!!");
            userRepository.save(user);
        }
        throw exception(USER, DUPLICATE_ENTITY, signupRequest.getUsername());
    }

    @Override
    public UserDto changeUsername(UserDto userDto) {
        Optional<User> user = userRepository.findByUsername(userDto.getUsername());
        if (user.isPresent()) {
            User userModel = user.get();
            userModel.setUsername(userDto.getUsername());
            return UserMapper.toDto(userRepository.save(userModel));
        }
        throw exception(USER, ENTITY_NOT_FOUND, userDto.getUsername());
    }

    @Override
    public UserDto changePassword(UserDto userDto, String newPassword) {
        Optional<User> user = userRepository.findByUsername(userDto.getUsername());
        if (user.isPresent()) {
            User userModel = user.get();
            userModel.setPassword(bCryptPasswordEncoder.encode(newPassword));
            return UserMapper.toDto(userRepository.save(userModel));
        }
        throw exception(USER, ENTITY_NOT_FOUND, userDto.getUsername());
    }

    @Override
    public UserDto getUserFromSession() {
        return null;
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
