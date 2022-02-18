package com.socialnetwork.domain;

import com.socialnetwork.rest.dto.CreateUserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface UserMapper {
    User map(CreateUserRequest request);
}
