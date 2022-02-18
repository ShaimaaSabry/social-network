package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.Post;
import com.socialnetwork.rest.dto.CreatePostRequest;

import java.util.List;
import java.util.Set;

public interface PostService {
    Set<Post> getAll(long userId);

    Post create(long userId, CreatePostRequest createPostRequest);
}
