package com.socialnetwork.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.socialnetwork.domain.EmailVerificationToken;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.CreateUserRequest;
import com.socialnetwork.dto.UpdateUserPasswordRequest;
import com.socialnetwork.dto.UpdateUserRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.dto.VerifyUserEmailRequest;
import com.socialnetwork.util.EmailService;
import com.socialnetwork.util.JwtUtil;
import com.socialnetwork.util.PhotoVerificationService;
import com.socialnetwork.util.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class UserControllerTest {
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
	
	@MockBean
	private EmailService emailService;
	
	@MockBean
	private StorageService profilePictureStorageService;

	@MockBean
	private PhotoVerificationService photoVerificationService;

	@Test
	public void whenSearchQueryIsValid_thenReturn200AndMatchingUserList() throws Exception {
		User user1 = new UserBuilder("user1@example.com").build();
		entityManager.persist(user1);
		User user2 = new UserBuilder("user2@example.com").build();
		entityManager.persist(user2);

		String accessToken = jwtUtil.generateAccessToken(user1.getId().toString());
		RequestBuilder request = MockMvcRequestBuilders.get("/users").param("q", "user1")
				 .header("Authorization", "Bearer " + accessToken);
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		List<UserResponse> userResponseList = objectMapper.readValue(response.getContentAsString(),
				new TypeReference<List<UserResponse>>() {
				});
		Assertions.assertEquals(1, userResponseList.size());
		Assertions.assertEquals(user1.getId(), userResponseList.get(0).getId());
	}

	@Test
	void whenCreateUserRequestIsValid_thenReturn201AndCreatedUser() throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1", "user1@example.com", "password", "password");
		RequestBuilder request = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		UserResponse userResponse = objectMapper.readValue(response.getContentAsString(), UserResponse.class);
		Assertions.assertEquals(createUserRequest.getFirstName(), userResponse.getFirstName());
		Assertions.assertEquals(createUserRequest.getLastName(), userResponse.getLastName());
		Assertions.assertEquals(createUserRequest.getEmail(), userResponse.getEmail());
	}
	
	@Test
	void whenCreateUserRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("firstName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("firstName").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("lastName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("lastName").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("email"));
		Assertions.assertTrue(errors.get("fieldErrors").get("email").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("password"));
		Assertions.assertTrue(errors.get("fieldErrors").get("password").contains("required"));
	}
	
	@Test
	void whenEmailFormatIsInvalid_thenReturn400AndErrors() throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1", "invalid format", "password", "password");
		RequestBuilder request = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("email"));
		Assertions.assertTrue(errors.get("fieldErrors").get("email").contains("well-formed email address"));
	}
	
	@Test
	void whenConfirmPasswordDoesNotMatchPassword_thenReturn400AndErrors() throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1", "user1@example.com", "password", "different password");
		RequestBuilder request = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("globalErrors").get("error").contains("Password and confirm password don't match."));
	}

	@Test
	void whenVerifyUserEmailRequestIsValid_thenReturn200AndSetEmailVerifiedToTrue() throws Exception {
		User user =new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		EmailVerificationToken emailVerificationToken = new EmailVerificationToken(null, user, UUID.randomUUID().toString());
		entityManager.persist(emailVerificationToken);

		VerifyUserEmailRequest verifyUserEmailRequest = new VerifyUserEmailRequest(emailVerificationToken.getToken());
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account/verifyemail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifyUserEmailRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		user = entityManager.find(User.class, user.getId());
		Assertions.assertTrue(user.isEmailVerified());
	}
	
	@Test
	void whenVerifyUserEmailRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account/verifyemail")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("token"));
		Assertions.assertTrue(errors.get("fieldErrors").get("token").contains("required"));
	}
	
	@Test
	void whenUpdateUserRequestIsValid_thenReturn200AndUpdatedUser() throws Exception {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);

		String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
		UpdateUserRequest updateUserRequest = new UpdateUserRequest("user1", "user1");
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateUserRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		UserResponse userResponse = objectMapper.readValue(response.getContentAsString(), UserResponse.class);
		Assertions.assertEquals(updateUserRequest.getFirstName(), userResponse.getFirstName());
		Assertions.assertEquals(updateUserRequest.getLastName(), userResponse.getLastName());
	}
	
	@Test
	void whenUpdateUserRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		String accessToken = jwtUtil.generateAccessToken("1");
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("firstName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("firstName").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("lastName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("lastName").contains("required"));
	}
	
	@Test
	void whenUpdateUserPasswordRequestIsValid_thenReturn200AndUpdateUserPassword() throws Exception {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);

		String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
		UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("password", "newpassword", "newpassword");
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account/password")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateUserPasswordRequest));
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
		user = entityManager.find(User.class, user.getId());
		Assertions.assertTrue(bCryptPasswordEncoder.matches(updateUserPasswordRequest.getNewPassword(), user.getPasswordHash()));
	}
	
	@Test
	void whenUpdateUserPasswordRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		String accessToken = jwtUtil.generateAccessToken("");
		RequestBuilder request = MockMvcRequestBuilders.put("/users/account/password")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();
		MockHttpServletResponse response = result.getResponse();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("currentPassword"));
		Assertions.assertTrue(errors.get("fieldErrors").get("currentPassword").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("newPassword"));
		Assertions.assertTrue(errors.get("fieldErrors").get("newPassword").contains("required"));
	}
}
