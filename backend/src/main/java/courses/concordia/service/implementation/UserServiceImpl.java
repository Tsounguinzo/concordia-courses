package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.User;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static courses.concordia.exception.EntityType.USER;
import static courses.concordia.exception.ExceptionType.DUPLICATE_ENTITY;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto signup(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        if (user == null) {
            user = new User(userDto.getUsername(),
                    userDto.getEmail(),
                    bCryptPasswordEncoder.encode(userDto.getPassword()));
            return UserMapper.toDto(userRepository.save(user));
        }
        throw exception(USER, DUPLICATE_ENTITY, userDto.getUsername());
    }

    @Override
    public UserDto findUserByUsername(String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        }
        throw exception(USER, ENTITY_NOT_FOUND, username);
    }

    @Override
    public UserDto updateProfile(UserDto userDto) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()));
        if (user.isPresent()) {
            User userModel = user.get();
            userModel.setUsername(userDto.getUsername());
            return UserMapper.toDto(userRepository.save(userModel));
        }
        throw exception(USER, ENTITY_NOT_FOUND, userDto.getUsername());
    }

    @Override
    public UserDto changePassword(UserDto userDto, String newPassword) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()));
        if (user.isPresent()) {
            User userModel = user.get();
            userModel.setPassword(bCryptPasswordEncoder.encode(newPassword));
            return UserMapper.toDto(userRepository.save(userModel));
        }
        throw exception(USER, ENTITY_NOT_FOUND, userDto.getUsername());
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
