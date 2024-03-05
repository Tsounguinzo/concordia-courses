package courses.concordia.service.implementation;


import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.User;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.AuthenticationService;
import courses.concordia.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse signup(SignupRequest signupRequest){
        var user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest signupRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signupRequest.getUsername(),
                        signupRequest.getPassword()
                )
        );
        var user = Optional.ofNullable(repository.findByUsername(signupRequest.getUsername()))
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
