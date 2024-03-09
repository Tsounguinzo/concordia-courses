package courses.concordia.service.implementation;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Token;
import courses.concordia.model.User;
import courses.concordia.repository.TokenRepository;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static courses.concordia.exception.EntityType.USER;
import static courses.concordia.exception.ExceptionType.DUPLICATE_ENTITY;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final EmailServiceImpl emailService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void signup(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            //Create user
            User user = new User(userDto.getUsername(),
                    userDto.getEmail(),
                    bCryptPasswordEncoder.encode(userDto.getPassword()),
                    userDto.isVerified());

            userRepository.save(user);

            //Email token
            Token token = new Token(user);
            //tokenRepository.deleteAll();
            tokenRepository.save(token);

            emailService.sendSimpleMailMessage(user.getUsername(), user.getEmail(), token.getToken());
        }
        throw exception(USER, DUPLICATE_ENTITY, userDto.getUsername());
    }

    public AuthenticationResponse authenticate(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if (user.isPresent()) {
            var jwtToken = jwtService.generateToken(user.get());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        throw exception(USER, ENTITY_NOT_FOUND, loginRequest.getUsername());
    }

    @Override
    public Boolean verifyToken(String token) {
        //System.out.println(token);
        Token tempToken = tokenRepository.findByToken(token);

        Optional<User> user = userRepository.findByUsername(tempToken.getUser().getUsername());
        if(user.isPresent()) {
            user.get().setVerified(true);
            userRepository.save(user.get());
            tokenRepository.delete(tempToken);
            return true;
        }
        return false;
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


    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }

    public boolean checkIfUserExist(String username){
        return userRepository.findByUsername(username).isPresent();
    }
}
