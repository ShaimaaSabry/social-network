package com.socialnetwork.web;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.CreatePostRequest;
import com.socialnetwork.dto.PostResponse;
import com.socialnetwork.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class PostControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Test
	public void whenCalled_thenReturn200AndOnlyUserPostList() throws Exception {
		User user1 = new UserBuilder("user1@example.com").build();
		entityManager.persist(user1);
		Post post1 = new Post(null, user1, "post1");
		entityManager.persist(post1);
		User user2 = new UserBuilder("user2@example.com").build();
		entityManager.persist(user2);
		Post post2 = new Post(null, user2, "post2");
		entityManager.persist(post2);

		String accessToken = jwtUtil.generateAccessToken(user1.getId().toString());
		RequestBuilder request = MockMvcRequestBuilders.get("/posts")
				 .header("Authorization", "Bearer " + accessToken);
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		List<PostResponse> postResponseList = objectMapper.readValue(response.getContentAsString(),
				new TypeReference<List<PostResponse>>() {
				});
		Assertions.assertEquals(1, postResponseList.size());
		Assertions.assertEquals(post1.getId(), postResponseList.get(0).getId());
	}
	
	@Test
	public void whenCreatePostRequestIsValid_thenReturn201AndCreatedPost() throws Exception {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);

		String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
		CreatePostRequest createPostRequest = new CreatePostRequest("post content.");
		RequestBuilder request = MockMvcRequestBuilders.post("/posts")
				 .header("Authorization", "Bearer " + accessToken)
				 .contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(createPostRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		PostResponse postResponse = objectMapper.readValue(response.getContentAsString(),
				PostResponse.class 
				);
		Assertions.assertEquals(createPostRequest.getContent(), postResponse.getContent());
	}
	
	@Test
	public void whenCreatePostRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);

		String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
		RequestBuilder request = MockMvcRequestBuilders.post("/posts")
				 .header("Authorization", "Bearer " + accessToken)
				 .contentType(MediaType.APPLICATION_JSON)
					.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("content"));
		Assertions.assertTrue(errors.get("fieldErrors").get("content").contains("required"));
	}
}
