package com.socialnetwork.repository;

import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.outport.PostRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class PostRepositoryImpl implements PostRepository {
    private final PostRepositoryBasic postRepository;
    private final PostEntityMapper mapper;

    PostRepositoryImpl(PostRepositoryBasic postRepository, PostEntityMapper mapper) {
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public Set<Post> findAllByUserId(long userId) {
        Collection<PostEntity> posts = postRepository.findAllByUserId(userId);

        return posts.stream()
                .map(mapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Post save(long userId, Post post) {
        PostEntity postEntity = mapper.map(post);
        postEntity.setUser(new UserEntity(userId));

        postRepository.save(postEntity);

        return mapper.map(postEntity);
    }
}
