package ec.com.sofka.appservice.user.commands.usecases;

import ec.com.sofka.appservice.user.commands.CreateUserCommand;
import ec.com.sofka.appservice.user.queries.responses.UserResponse;
import ec.com.sofka.gateway.UserRepository;
import ec.com.sofka.gateway.dto.UserDTO;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import ec.com.sofka.user.Role;
import reactor.core.publisher.Mono;

public class CreateUserUseCase implements IUseCaseExecute<CreateUserCommand, UserResponse> {
    private final UserRepository userRepository;


    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserResponse> execute(CreateUserCommand request) {
        if (!isValidRole(request.getRole())) {
            return Mono.error(new IllegalArgumentException("The role is invalid"));
        }

        return userRepository.findByEmail(request.getEmail())
                .flatMap(existingUser -> Mono.<UserResponse>error(new RuntimeException("User with provided email already exists")))
                .switchIfEmpty(
         userRepository.save(
                        new UserDTO(null,
                                request.getFirstname(),
                                request.getLastname(),
                                request.getEmail(),
                                request.getPassword(),
                                request.getRole()))
                .map(userDTO -> new UserResponse(
                        userDTO.getFirstname(),
                        userDTO.getLastname(),
                        userDTO.getEmail(),
                        userDTO.getPassword(),
                        userDTO.getRole()))
                );

    }

    private boolean isValidRole(Role role) {
        try {
            Role.valueOf(String.valueOf(role));
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The role is not valid");
        }
    }
}
