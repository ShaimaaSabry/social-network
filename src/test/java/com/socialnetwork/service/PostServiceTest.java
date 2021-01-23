package com.socialnetwork.service;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.CreatePostRequest;
import com.socialnetwork.dto.PostResponse;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class PostServiceTest {
	@Autowired
	private PostService postService;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void whenUserIdIsValid_ReturnOnlyUserPostList() {
		User user1 = new UserBuilder("user1@example.com").build();
		entityManager.persist(user1);
		Post post1 = new Post(null, user1, "post1");
		entityManager.persist(post1);
		User user2 = new UserBuilder("user2@example.com").build();
		entityManager.persist(user2);
		Post post2 = new Post(null, user2, "post2");
		entityManager.persist(post2);
		
		List<PostResponse> postResponseList = postService.getAll(user1.getId());
		
		Assertions.assertEquals(1, postResponseList.size());
		Assertions.assertEquals(post1.getId(), postResponseList.get(0).getId());
	}

	@Test
	public void whenCreatePostRequestIsValid_thenCreatePost() {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		
		CreatePostRequest createPostRequest = new CreatePostRequest("post content.");
		PostResponse postResponse = postService.create(user.getId(), createPostRequest);
		
		Post post = entityManager.find(Post.class, postResponse.getId());
		Assertions.assertEquals(user.getId(), post.getUser().getId());
		Assertions.assertEquals(createPostRequest.getContent(), post.getContent());
	}
}
