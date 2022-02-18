package com.socialnetwork.domain.outport;

import com.socialnetwork.domain.Post;

import java.util.Collection;
import java.util.Set;

public interface PostRepository {
    Set<Post> findAllByUserId(long userId);
    Post save(long userId, Post post);
}
