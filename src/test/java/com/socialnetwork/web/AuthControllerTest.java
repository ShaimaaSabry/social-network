package com.socialnetwork.web;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.AuthResponse;
import com.socialnetwork.dto.RefreshTokenRequest;
import com.socialnetwork.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Test
	public void whenEmailAndPasswordAreValid_thenReturn200AndAccessTokenAndRefreshToken() throws Exception {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);

		AuthRequest authRequest = new AuthRequest("user1@example.com", "password");
		RequestBuilder request = MockMvcRequestBuilders.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthResponse.class);
		Assertions.assertEquals(user.getId(), authResponse.getUser().getId());
	}
	
	@Test
	public void whenAuthRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("email"));
		Assertions.assertTrue(errors.get("fieldErrors").get("email").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("password"));
		Assertions.assertTrue(errors.get("fieldErrors").get("password").contains("required"));
	}
	
	@Test
	public void whenRefrehTokenIsValid_thenReturn200AndAccessTokenAndRefreshToken() throws Exception {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);

		String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
		RequestBuilder request = MockMvcRequestBuilders.post("/auth/tokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshTokenRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthResponse.class);
		Assertions.assertEquals(user.getId(), authResponse.getUser().getId());
	}
	
	@Test
	public void whenRefrehTokenRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/auth/tokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("refreshToken"));
		Assertions.assertTrue(errors.get("fieldErrors").get("refreshToken").contains("required"));
	}
}
