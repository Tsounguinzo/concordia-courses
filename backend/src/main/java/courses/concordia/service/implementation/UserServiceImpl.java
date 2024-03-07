package courses.concordia.service.implementation;

import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.User;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse signup(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        if (user == null) {
            user = new User(userDto.getUsername(),
                    userDto.getEmail(),
                    bCryptPasswordEncoder.encode(userDto.getPassword()),
                    userDto.getVerified());
            var jwtToken = jwtService.generateToken(user);
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        throw exception(USER, DUPLICATE_ENTITY, signupRequest.getUsername());
    }

    public AuthenticationResponse authenticate(LoginRequest LoginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        LoginRequest.getUsername(),
                        LoginRequest.getPassword()
                )
        );
        var user = Optional.ofNullable(userRepository.findByUsername(LoginRequest.getUsername()))
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
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

    public Boolean checkIfUserExist(String username){
        return findUserByUsername(username) != null;
    }
}
