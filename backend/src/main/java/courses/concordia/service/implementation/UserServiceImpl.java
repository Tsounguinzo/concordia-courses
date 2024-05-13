package courses.concordia.service.implementation;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.RtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.dto.mapper.UserMapper;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Token;
import courses.concordia.model.User;
import courses.concordia.repository.TokenRepository;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.*;
import courses.concordia.service.implementation.token.UUIDTokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    private final CookieService cookieService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtConfigProperties jwtConfigProperties;
    private final RtConfigProperties rtConfigProperties;


    /**
     * Registers a new user in the system with the provided user details.
     * This method also checks for duplicate username or email, encodes the password,
     * saves the user, sends a verification email, and automatically logs in the user upon successful signup.
     *
     * @param userDto User details provided for registration.
     * @return AuthenticationResponse containing JWT and refresh tokens.
     */
    @Override
    @Transactional
    public AuthenticationResponse signup(UserDto userDto) {
        log.info("Signing up user: {}", userDto.getUsername());

        checkUserExistence(userDto.getUsername(), userDto.getEmail());
        User user = createUserFromDto(userDto);
        Token token = createAndSaveTokenForUser(user);

        emailService.sendSimpleMailMessage(user.getUsername(), user.getEmail(), token.getToken());
        authenticate(userDto.getUsername(), userDto.getPassword());

        return generateAuthenticationResponse(user);
    }

    /**
     * Verifies if a token is valid and not expired.
     *
     * @param token The token to verify.
     * @return true if the token is valid and not expired, false otherwise.
     */
    @Override
    public boolean verifyToken(String token) {
        Token foundToken = fetchToken(token);
        return verifyAndActivateUser(foundToken);
    }

    /**
     * Resends a verification token to the user if they are not already verified.
     *
     * @param username The username of the user to resend the token to.
     */
    @Override
    public void resendToken(String username) {
        log.info("Resending token to user: {}", username);
        User user = getUser(username);

        if (user.isVerified()) {
            throw exception(USER, CUSTOM_EXCEPTION, username + " is already verified.");
        }

        Token token = tokenRepository.findByUserId(user.get_id())
                .filter(t -> !t.isExpired())
                .orElseGet(() -> createAndSaveTokenForUser(user));

        emailService.sendNewTokenMailMessage(user.getUsername(), user.getEmail(), token.getToken());
    }

    /**
     * Changes the username for an existing user.
     *
     * @param newUsername       The new username for the user.
     * @param originalUsername  The current username of the user.
     * @return UserDto with updated username.
     */
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

    /**
     * Changes the password for a user identified by a reset token.
     *
     * @param token        The token identifying the user.
     * @param newPassword  The new password to set for the user.
     */
    @Override
    public void changePassword(Token token, String newPassword) {
        String username = verifyPasswordResetToken(token.getToken());
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));
        updateUserPassword(user, newPassword);
        tokenRepository.delete(token);
        emailService.sendResetPasswordConfirmationMailMessage(user.getUsername(), user.getEmail());
    }

    /**
     * Verifies a password reset token and returns the associated username.
     *
     * @param token The password reset token to verify.
     * @return The username associated with the valid token.
     */
    @Override
    public String verifyPasswordResetToken(String token) {
        log.info("Verifying reset password token: {}", token);
        Token foundToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> exception(TOKEN, CUSTOM_EXCEPTION, "Invalid or expired token."));

        User user = userRepository.findById(foundToken.getUserId())
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));

        return user.getUsername();
    }
    /**
     * Retrieves the currently authenticated user.
     *
     * @return The currently authenticated User, or null if not authenticated.
     */
    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
    }

    /**
     * Initiates a forgot password request for a user, generating and sending a reset token.
     *
     * @param username The username of the user requesting password reset.
     */
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

    /**
     * Signs out the currently authenticated user by clearing the token cookies.
     *
     * @param request  The incoming HTTP request.
     * @param response The outgoing HTTP response.
     */
    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieService.getTokenFromCookie(request, jwtConfigProperties.getTokenName());
        String refreshToken = cookieService.getTokenFromCookie(request, rtConfigProperties.getTokenName());

        if (accessToken == null && refreshToken == null) {
            // If both tokens are missing, there's nothing to do. User might already be signed out.
            throw exception(USER, CUSTOM_EXCEPTION, "No active session found or already signed out.");
        }

        blacklistTokenIfNeeded(accessToken, TokenType.ACCESS_TOKEN);
        blacklistTokenIfNeeded(refreshToken, TokenType.REFRESH_TOKEN);

        cookieService.clearTokenCookies(response);
    }

    /**
     * Signs in a user with the provided login credentials, generating and setting token cookies.
     *
     * @param loginRequest The login credentials for the user.
     * @param response     The outgoing HTTP response.
     */
    @Override
    public void signIn(LoginRequest loginRequest, HttpServletResponse response) {
        authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        User user = getAuthenticatedUser();
        AuthenticationResponse res = generateAuthenticationResponse(user);
        cookieService.addTokenCookies(response, res);
    }

    private void blacklistTokenIfNeeded(String token, TokenType tokenType) {
        if (token != null && !tokenBlacklistService.isTokenBlacklisted(token)) {
            Date tokenExpiration = jwtService.extractExpiration(token, tokenType);
            long tokenDurationInSeconds = Math.max(0, (tokenExpiration.getTime() - System.currentTimeMillis()) / 1000);
            if (tokenDurationInSeconds > 0) { // Only blacklist if token is not already expired
                tokenBlacklistService.blacklistToken(token, tokenDurationInSeconds);
            }
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CustomExceptionFactory.throwCustomException(entityType, exceptionType, args);
    }

    private User createUserFromDto(UserDto userDto) {
        User user = new User()
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .setVerified(false);
        return userRepository.save(user);
    }

    private AuthenticationResponse generateAuthenticationResponse(User user) {
        String jwtToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    private void authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", username, e);
            throw exception(USER, CUSTOM_EXCEPTION, "Wrong credentials.");
        } catch (DisabledException e) {
            log.error("Authentication failed, account is disabled: {}", username, e);
            throw exception(USER, CUSTOM_EXCEPTION, "Account is disabled.");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", username, e);
            throw exception(USER, CUSTOM_EXCEPTION, "Authentication failed.");
        }
    }

    private Token fetchToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> exception(TOKEN, CUSTOM_EXCEPTION, "Invalid or expired token."));
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> exception(USER, ENTITY_NOT_FOUND));
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private boolean verifyAndActivateUser(Token token) {
        User user = getUserById(token.getUserId());
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            tokenRepository.delete(token);
            return true;
        }
        return false;
    }

    private Token createAndSaveTokenForUser(User user) {
        Token t = tokenRepository.findByUserId(user.get_id()).orElse(null);
        if(t != null && t.isExpired()) {
            tokenRepository.delete(t);
        }
        Token newToken = new Token(user);
        return tokenRepository.save(newToken);
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

    private void checkUserExistence(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw exception(USER, DUPLICATE_ENTITY);
        }
    }
}
