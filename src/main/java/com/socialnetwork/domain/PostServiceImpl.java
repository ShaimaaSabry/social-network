package com.socialnetwork.domain;

import com.socialnetwork.domain.inport.PostService;
import com.socialnetwork.domain.outport.PostRepository;
import com.socialnetwork.repository.PostRepositoryBasic;
import com.socialnetwork.rest.dto.CreatePostRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Set<Post> getAll(long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    public Post create(long userId, CreatePostRequest createPostRequest) {
        Post post = new Post(null, null, createPostRequest.getContent());
        post = postRepository.save(userId, post);
        return post;
    }
}
