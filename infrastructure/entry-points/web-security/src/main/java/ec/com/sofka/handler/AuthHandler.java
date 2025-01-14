package ec.com.sofka.handler;

import ec.com.sofka.appservice.user.commands.CreateUserCommand;
import ec.com.sofka.appservice.user.commands.usecases.CreateUserUseCase;
import ec.com.sofka.data.AuthRequest;
import ec.com.sofka.data.AuthResponse;
import ec.com.sofka.data.RegisterRequest;
import ec.com.sofka.JwtService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class AuthHandler {
    private final CreateUserUseCase createUserUseCase;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;



    public AuthHandler(CreateUserUseCase createUserUseCase, ReactiveAuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.createUserUseCase = createUserUseCase;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<RegisterRequest> register(RegisterRequest userReqDTO) {
        return createUserUseCase.execute(
                new CreateUserCommand(
                        userReqDTO.getFirstname(),
                        userReqDTO.getLastname(),
                        userReqDTO.getEmail(),
                        passwordEncoder.encode(userReqDTO.getPassword()),
                        userReqDTO.getRole()
                )
        ).map(res -> new RegisterRequest(res.getFirstname(), res.getLastname(), res.getEmail(), res.getPassword(), res.getRole()));
    }

    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(auth -> {
                    var userDetails = (UserDetails) auth.getPrincipal();
                    return getAuthResponse(userDetails);
                });
    }

    private AuthResponse getAuthResponse(UserDetails userDetails) {
        var extraClaims = extractAuthorities("roles", userDetails);

        var jwtToken = jwtService.generateToken(userDetails, extraClaims);
        return new AuthResponse(jwtToken);
    }

    private Map<String, Object> extractAuthorities(String key, UserDetails userDetails) {
        Map<String, Object> authorities = new HashMap<>();

        authorities.put(key,
                userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));

        return authorities;
    }

}