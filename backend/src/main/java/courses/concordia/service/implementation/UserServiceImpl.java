package courses.concordia.service.implementation;

import courses.concordia.config.TokenType;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.exception.ExceptionHelper;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Token;
import courses.concordia.model.User;
import courses.concordia.repository.TokenRepository;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.EmailService;
import courses.concordia.service.JwtService;
import courses.concordia.service.TokenGenerator;
import courses.concordia.service.UserService;
import courses.concordia.service.implementation.token.UUIDTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static courses.concordia.exception.EntityType.TOKEN;
import static courses.concordia.exception.EntityType.USER;
import static courses.concordia.exception.ExceptionType.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public AuthenticationResponse signup(UserDto userDto) {
        log.info("Attempting to signup user: {}", userDto.getUsername());

        userRepository.findByUsername(userDto.getUsername()).ifPresent(u -> {
            throw exception(USER, DUPLICATE_ENTITY);
        });

        userRepository.findByEmail(userDto.getEmail()).ifPresent(u -> {
            throw exception(USER, DUPLICATE_ENTITY);
        });

        User user = new User()
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .setVerified(userDto.isVerified());

        user = userRepository.save(user);

        Token token = createAndSaveNewToken(user);

        emailService.sendSimpleMailMessage(user.getUsername(), user.getEmail(), token.getToken());
        authenticate(userDto.getUsername(), userDto.getPassword());
        var jwtToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);
        var refreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);
            String refreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);


            log.info("Authentication successful for user: {}", loginRequest.getUsername());

            return new AuthenticationResponse(jwtToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw exception(USER, CUSTOM_EXCEPTION, "wrong username or password.");
        } catch (DisabledException e) {
            log.error("Authentication failed, account is disabled: {}", loginRequest.getUsername(), e);
            throw exception(USER, CUSTOM_EXCEPTION, "Account is disabled.");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw exception(USER, CUSTOM_EXCEPTION, "Authentication failed.");
        }
    }

    @Override
    public boolean verifyToken(String token) {
        log.info("Verifying token: {}", token);
        Token foundToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> exception(TOKEN, CUSTOM_EXCEPTION, "Invalid or expired token."));
        return verifyAndActivateUser(foundToken);
    }

    @Override
    public boolean isUserVerified(String username) {
        log.info("Checking verification status for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND, username));
        return user.isVerified();
    }

    @Override
    public void resendToken(String username) {
        log.info("Resending token to user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));

        if (user.isVerified()) {
            throw exception(USER, CUSTOM_EXCEPTION, username + " is already verified.");
        }

        Token token = tokenRepository.findByUserId(user.get_id())
                .filter(t -> !t.isExpired())
                .orElseGet(() -> createAndSaveNewToken(user));

        emailService.sendNewTokenMailMessage(user.getUsername(), user.getEmail(), token.getToken());
    }

    @Override
    public UserDto changeUsername(String newUsername, String originalUsername) {
        log.info("Changing username for user: {}", originalUsername);
        User user = userRepository.findByUsername(originalUsername)
                .orElseThrow(() -> exception(USER, DUPLICATE_ENTITY, originalUsername));

        if (userRepository.existsById(newUsername)) {
            throw exception(USER, CUSTOM_EXCEPTION, "Username already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public void changePassword(Token token, String newPassword) {
        String username = verifyPasswordResetToken(token.getToken());
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        tokenRepository.delete(token);
        emailService.sendResetPasswordConfirmationMailMessage(user.getUsername(), user.getEmail());
    }

    @Override
    public String getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::get_id)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));
    }

    @Override
    public String verifyPasswordResetToken(String token) {
        log.info("Verifying reset password token: {}", token);
        Token foundToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> exception(TOKEN, CUSTOM_EXCEPTION, "Invalid or expired token."));

        User user = userRepository.findById(foundToken.getUserId())
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));

        return user.getUsername();
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
    }

    @Override
    public void forgotPassword(String username) {
        log.info("Forgot password request for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));

        Token token = tokenRepository.findByUserId(user.get_id())
                .filter(t -> !t.isExpired())
                .orElseGet(() -> createAndSaveNewToken(user, new UUIDTokenGenerator()));

        emailService.sendResetPasswordMailMessage(user.getUsername(), user.getEmail(), token.getToken());
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ExceptionHelper.throwException(entityType, exceptionType, args);
    }

    public boolean checkIfUserExist(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", username, e);
            throw exception(USER, CUSTOM_EXCEPTION, "Authentication failed");
        }
    }

    private boolean verifyAndActivateUser(Token token) {
        String userId = token.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            tokenRepository.delete(token);
            log.info("User verified: {}", user.getUsername());
            return true;
        }
        log.info("User already verified: {}", user.getUsername());
        return false;
    }

    private Token createAndSaveNewToken(User user) {
        Token t = tokenRepository.findByUserId(user.get_id()).orElse(null);
        if(t != null && t.isExpired()) {
            tokenRepository.delete(t);
        }
        Token newToken = new Token(user);
        tokenRepository.save(newToken);
        return newToken;
    }

    private Token createAndSaveNewToken(User user, TokenGenerator tokenGenerator) {
        Token t = tokenRepository.findByUserId(user.get_id()).orElse(null);
        if(t != null && t.isExpired()) {
            tokenRepository.delete(t);
        }
        Token newToken = new Token(user, tokenGenerator);
        tokenRepository.save(newToken);
        return newToken;
    }
}
