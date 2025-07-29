package com.example.franchise_api.infrastructure.entrypoints.mapper;

import com.example.franchise_api.domain.model.User;
import com.example.franchise_api.infrastructure.entrypoints.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    User userDTOToUser(UserDTO userDTO);
}
