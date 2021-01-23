package com.socialnetwork.service;

import java.util.List;

import com.socialnetwork.dto.CreatePostRequest;
import com.socialnetwork.dto.PostResponse;

public interface PostService {
	List<PostResponse> getAll(long userId);
	PostResponse create(long userId, CreatePostRequest createPostRequest);
}
