package com.socialnetwork.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.User;
import com.socialnetwork.dto.CreatePostRequest;
import com.socialnetwork.dto.PostResponse;
import com.socialnetwork.repository.PostRepository;

@Service
public class PostServiceImp implements PostService {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<PostResponse> getAll(long userId) {
		List<Post> postList = postRepository.findAllByUserId(userId);

		List<PostResponse> postResponseList = postList.stream().map(post -> modelMapper.map(post, PostResponse.class))
				.collect(Collectors.toList());
		return postResponseList;
	}

	@Override
	public PostResponse create(long userId, CreatePostRequest createPostRequest) {
		Post post = modelMapper.map(createPostRequest, Post.class);
		User user = new User();
		user.setId(userId);
		post.setUser(user);
		postRepository.save(post);

		PostResponse postResponse = modelMapper.map(post, PostResponse.class);
		return postResponse;
	}

}
