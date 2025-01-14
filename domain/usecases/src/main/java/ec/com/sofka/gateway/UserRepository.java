package ec.com.sofka.gateway;

import ec.com.sofka.gateway.dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<UserDTO> save(UserDTO user);
    Mono<UserDTO> findByEmail(String email);
}
