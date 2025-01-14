package ec.com.sofka.user;

import ec.com.sofka.appservice.user.commands.CreateUserCommand;
import ec.com.sofka.appservice.user.commands.usecases.CreateUserUseCase;
import ec.com.sofka.appservice.user.queries.responses.UserResponse;
import ec.com.sofka.gateway.UserRepository;
import ec.com.sofka.gateway.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        createUserUseCase = new CreateUserUseCase(userRepository);
    }

    @Test
    public void testCreateUser_Positive() {
        // Crear datos de entrada
        CreateUserCommand request = new CreateUserCommand("John", "Doe", "john.doe@example.com", "password123", Role.USER);

        UserDTO userDTO = new UserDTO(null, "John", "Doe", "john.doe@example.com", "password123", Role.USER);

        // Simular que el repositorio no encuentra el usuario por correo
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Mono.empty());

        // Simular que el repositorio guarda el usuario correctamente
        when(userRepository.save(any(UserDTO.class))).thenReturn(Mono.just(userDTO));

        // Llamar al método `execute` y verificar la respuesta
        Mono<UserResponse> result = createUserUseCase.execute(request);

        StepVerifier.create(result)
                .expectNextMatches(userResponse -> {
                    // Verificar que el mapeo de datos es correcto
                    assertEquals("John", userResponse.getFirstname());
                    assertEquals("Doe", userResponse.getLastname());
                    assertEquals("john.doe@example.com", userResponse.getEmail());
                    assertEquals("password123", userResponse.getPassword());
                    assertEquals(Role.USER, userResponse.getRole());
                    return true;
                })
                .expectComplete()
                .verify();

        // Verificar que los métodos del repositorio fueron llamados correctamente
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, times(1)).save(any(UserDTO.class));
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        // Crear datos de entrada
        CreateUserCommand request = new CreateUserCommand("John", "Doe", "john.doe@example.com", "password123", Role.USER);

        // Simular que el repositorio ya encuentra el usuario por correo
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Mono.just(new UserDTO(null, "John", "Doe", "john.doe@example.com", "password123", Role.USER)));

        // Llamar al método `execute` y verificar que se lanza un error
        Mono<UserResponse> result = createUserUseCase.execute(request);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        // Verificar que el repositorio fue llamado correctamente
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    public void testCreateUser_InvalidRole() {
        // Crear datos de entrada con un rol inválido
        CreateUserCommand request = new CreateUserCommand("John", "Doe", "john.doe@example.com", "password123", null);

        // Llamar al método `execute` y verificar que se lanza un error
        Mono<UserResponse> result = createUserUseCase.execute(request);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}