package ec.com.sofka.mapper;

import ec.com.sofka.data.UserEntity;
import ec.com.sofka.gateway.dto.UserDTO;
import ec.com.sofka.user.Role;

public class UserMapper {
    public static UserEntity toUser(final UserDTO dto) {
        return new UserEntity(
                null,
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );
    }

    public static UserDTO toUserDTO(final UserEntity entity) {
        return new UserDTO(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole()
        );
    }
}
