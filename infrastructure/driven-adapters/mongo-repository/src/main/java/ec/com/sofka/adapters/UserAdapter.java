package ec.com.sofka.adapters;

import ec.com.sofka.database.bank.UserMongoRepository;
import ec.com.sofka.gateway.UserRepository;
import ec.com.sofka.gateway.dto.UserDTO;
import ec.com.sofka.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserAdapter implements UserRepository {

    private final UserMongoRepository userRepository;

    public UserAdapter(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDTO> save(UserDTO user) {
        return userRepository.save(UserMapper.toUser(user)).map(UserMapper::toUserDTO);
    }

    @Override
    public Mono<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserMapper::toUserDTO);
    }

}
