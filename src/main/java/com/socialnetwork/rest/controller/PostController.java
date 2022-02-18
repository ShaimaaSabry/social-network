package com.socialnetwork.rest.controller;

import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.inport.PostService;
import com.socialnetwork.rest.dto.CreatePostRequest;
import com.socialnetwork.rest.dto.PostResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@Api(tags = "Posts")
class PostController {
    @Autowired
    PostService postService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get Posts", notes = "Get a list of your posts.")
    List<PostResponse> getAll(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Set<Post> posts = postService.getAll(userId);

        return posts.stream().map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ApiOperation(value = "Create Post", notes = "Create a new post.")
    ResponseEntity<PostResponse> create(Authentication authentication, @Validated @RequestBody CreatePostRequest createPostRequest) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Post post = postService.create(userId, createPostRequest);

        PostResponse postResponse = modelMapper.map(post, PostResponse.class);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{postId}")
                .buildAndExpand(postResponse.getId()).toUri();
        return ResponseEntity.created(location).body(postResponse);
    }
}
