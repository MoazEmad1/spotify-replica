package com.example.mapper;

import com.example.domain.User;
import com.example.dto.ReadUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ReadUserDTO readUserDTOToUser(User entity);

}
