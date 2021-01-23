package com.socialnetwork.web;

import java.net.URI;
import java.util.List;

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

import com.socialnetwork.dto.CreatePostRequest;
import com.socialnetwork.dto.PostResponse;
import com.socialnetwork.service.PostService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/posts")
@Api(tags = "Posts")
public class PostController {
	@Autowired
	PostService postService;

	@GetMapping
	@ApiOperation(value = "Get Posts", notes = "Get a list of your posts.")
	public List<PostResponse> getAll(Authentication authentication) {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		return postService.getAll(userId);
	}
	
	@PostMapping
	@ApiOperation(value = "Create Post", notes = "Create a new post.")
	public ResponseEntity<PostResponse> create(Authentication authentication, @Validated @RequestBody CreatePostRequest createPostRequest) {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		PostResponse postResponse = postService.create(userId, createPostRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{postId}")
				.buildAndExpand(postResponse.getId()).toUri();
		return ResponseEntity.created(location).body(postResponse);
	}
}
