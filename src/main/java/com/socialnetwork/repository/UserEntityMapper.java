package com.socialnetwork.repository;

import com.socialnetwork.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface UserEntityMapper {
    User map(UserEntity user);
    UserEntity map(User user);
}
