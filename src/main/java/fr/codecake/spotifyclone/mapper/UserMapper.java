package fr.codecake.spotifyclone.mapper;

import fr.codecake.spotifyclone.domain.User;
import fr.codecake.spotifyclone.dto.ReadUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ReadUserDTO readUserDTOToUser(User entity);

}
