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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Value("${app.jwt-key}")
    private String jwtSecret;
    @Value("${app.jwt-exp:604800000}")
    private String jwtExpirationMs;

    @Override
    public UserDto signup(SignupRequest signupRequest) {
        User user = userRepository.findByUsername(signupRequest.getUsername());
        if (user == null) {
            user = new User()
                    .setUsername(signupRequest.getUsername())
                            .setEmail(signupRequest.getEmail())
                                    .setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));

            emailService.sendSimpleEmail(signupRequest.getEmail(), "Welcome", "This is a welcome email for your!!");
            return UserMapper.toDto(userRepository.save(user));
        }
        throw exception(USER, DUPLICATE_ENTITY, signupRequest.getUsername());
    }

    @Override
    public UserDto changeUsername(UserDto userDto) {
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

    @Override
    public UserDto getUserFromSession() {
        return null;
    }

    public String generateTokenForUser(UserDto userDto) {
        return Jwts.builder()
                .setSubject(userDto.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
