package com.socialnetwork.repository;

import com.socialnetwork.domain.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PostEntityMapper {
    Post map(PostEntity post);
    PostEntity map(Post post);
}
