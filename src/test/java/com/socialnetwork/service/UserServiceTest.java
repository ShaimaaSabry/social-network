package com.socialnetwork.service;

import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.socialnetwork.domain.EmailVerificationToken;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.CreateUserRequest;
import com.socialnetwork.dto.UpdateUserPasswordRequest;
import com.socialnetwork.dto.UpdateUserRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.dto.VerifyUserEmailRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.util.EmailService;
import com.socialnetwork.util.PhotoVerificationService;
import com.socialnetwork.util.StorageService;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class UserServiceTest {
	@Autowired
	private UserService userService;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@MockBean
	private EmailService emailService;
	
	@MockBean
	private StorageService profilePictureStorageService;

	@MockBean
	private PhotoVerificationService photoVerificationService;
	
	@Test
	public void whenSearchQueryMatchesEmail_thenReturnOnlyMatchingUsersList() {
		User user1 = new UserBuilder("user1@example.com").build();
		entityManager.persist(user1);
		User user2 = new UserBuilder("user2@example.com").build();
		entityManager.persist(user2);
		
		List<UserResponse> userResponseList = userService.getAll("user1");
		
		Assertions.assertEquals(1, userResponseList.size());
		Assertions.assertEquals(user1.getId(), userResponseList.get(0).getId());
	}
	
	@Test
	public void whenSearchQueryMatchesFirstName_thenReturnOnlyMatchingUsersList() {
		User user1 = new UserBuilder("user1@example.com").firstName("user 1 matches").build();
		entityManager.persist(user1);
		User user2 = new UserBuilder("user2@example.com").firstName("user2").build();
		entityManager.persist(user2);
		
		List<UserResponse> userResponseList = userService.getAll("matches");
		
		Assertions.assertEquals(1, userResponseList.size());
		Assertions.assertEquals(user1.getId(), userResponseList.get(0).getId());
	}
	
	@Test
	public void whenSearchQueryMatchesLastName_thenReturnOnlyMatchingUsersList() {
		User user1 = new UserBuilder("user1@example.com").lastName("user1 matches").build();
		entityManager.persist(user1);
		User user2 = new UserBuilder("user2@example.com").lastName("user2").build();
		entityManager.persist(user2);
		
		List<UserResponse> userResponseList = userService.getAll("matches");
		
		Assertions.assertEquals(1, userResponseList.size());
		Assertions.assertEquals(user1.getId(), userResponseList.get(0).getId());
	}
	
	@Test
	public void whenUserIdIsValid_thenReturnUser() throws InvalidUserIdException {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		
		UserResponse userResponse = userService.getOneById(user.getId());
		
		Assertions.assertEquals(user.getId(), userResponse.getId());
	}
	
	@Test
	public void whenUserIdIsInvalid_thenGetOneByIdThrowsInvalidUserIdException() throws InvalidUserIdException {
		Assertions.assertThrows(InvalidUserIdException.class, () -> {
			userService.getOneById(1);
		});
	}
	
	@Test
	public void whenEmailAndPasswordAreValid_thenReturnUser() throws InvalidUserCredentialsException {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);
		
		AuthRequest authRequest = new AuthRequest("user1@example.com", "password");
		UserResponse userResponse = userService.getOneByEmailAndPassword(authRequest);
		
		Assertions.assertEquals(user.getId(), userResponse.getId());
	}
	
	@Test
	public void whenEmailIsInvalid_thenThrowInvalidUserCredentialsException() {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);
		
		Assertions.assertThrows(InvalidUserCredentialsException.class, () -> {
			AuthRequest authRequest = new AuthRequest("incorrect@example.com", "password");
			userService.getOneByEmailAndPassword(authRequest);
		});
	}
	
	@Test
	public void whenPasswordIsInvalid_thenThrowInvalidUserCredentialsException() {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);
		
		Assertions.assertThrows(InvalidUserCredentialsException.class, () -> {
			AuthRequest authRequest = new AuthRequest("incorrect@example.com", "password");
			userService.getOneByEmailAndPassword(authRequest);
		});
	}
	
	@Test
	public void whenCreateUserRequestIsValid_thenCreateUser() throws AddressException, MessagingException {
		CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1", "user1@example.com", "password", "password");
		UserResponse userResponse = userService.create(createUserRequest);
		
		User user = entityManager.find(User.class, userResponse.getId());
		Assertions.assertEquals(createUserRequest.getFirstName(), user.getFirstName());
		Assertions.assertEquals(createUserRequest.getLastName(), user.getLastName());
		Assertions.assertEquals(createUserRequest.getEmail(), user.getEmail());
		Assertions.assertFalse(user.isEmailVerified());
		Assertions.assertTrue(bCryptPasswordEncoder.matches(createUserRequest.getPassword(), user.getPasswordHash()));
	}
	
	@Test
	public void whenVerifyUserEmailRequestIsValid_thenSetEmailVerifiedToTrue() {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		EmailVerificationToken emailVerificationToken = new EmailVerificationToken(null, user, UUID.randomUUID().toString());
		entityManager.persist(emailVerificationToken);
		
		VerifyUserEmailRequest verifyUserEmailRequest = new VerifyUserEmailRequest(emailVerificationToken.getToken());
		boolean result = userService.verifyEmail(verifyUserEmailRequest);
		
		Assertions.assertTrue(result);
		user = entityManager.find(User.class, user.getId());
		Assertions.assertTrue(user.isEmailVerified());
	}
	
	@Test
	public void whenVerifyUserEmailRequestIsInvalid_thenReturnFalse() {
		VerifyUserEmailRequest verifyUserEmailRequest = new VerifyUserEmailRequest("invalid token");
		boolean result = userService.verifyEmail(verifyUserEmailRequest);
		
		Assertions.assertFalse(result);
	}
	
	@Test
	public void whenUpdateUserRequestIsValid_thenUpdateFirstNameAndLastName() throws InvalidUserIdException {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		
		UpdateUserRequest updateUserRequest = new UpdateUserRequest("updated first name", "updated last name");
		userService.update(user.getId(), updateUserRequest);
		
		user = entityManager.find(User.class, user.getId());
		Assertions.assertEquals(updateUserRequest.getFirstName(), user.getFirstName());
		Assertions.assertEquals(updateUserRequest.getLastName(), user.getLastName());
	}
	
	@Test
	public void whenUserIdIsInvalid_thenUpdateThrowsInvalidUserIdException() throws InvalidUserIdException {
		Assertions.assertThrows(InvalidUserIdException.class, () -> {
			UpdateUserRequest updateUserRequest = new UpdateUserRequest("updated first name", "updated last name");
			userService.update(1, updateUserRequest);
		});
	}
	
	@Test
	public void whenUpdatePasswordRequestIsValid_thenUpdatePassword() throws InvalidUserIdException {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);
		
		UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("password", "newpassword", "newpassword");
		boolean result = userService.updatePassword(user.getId(), updateUserPasswordRequest);
		
		Assertions.assertTrue(result);
		user = entityManager.find(User.class, user.getId());
		Assertions.assertTrue(bCryptPasswordEncoder.matches(updateUserPasswordRequest.getNewPassword(), user.getPasswordHash()));
	}
	
	@Test
	public void whenCurrentPasswordIsInvalid_thenDoNotUpdatePassword() throws InvalidUserIdException {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);
		
		UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("incorrect password", "newpassword", "newpassword");
		boolean result = userService.updatePassword(user.getId(), updateUserPasswordRequest);
		
		Assertions.assertFalse(result);
		user = entityManager.find(User.class, user.getId());
		Assertions.assertTrue(bCryptPasswordEncoder.matches("password", user.getPasswordHash()));
	}

	@Test
	public void whenUserIdIsInvalid_thenUpdatePasswordThrowsInvalidUserIdException() throws InvalidUserIdException {
		Assertions.assertThrows(InvalidUserIdException.class, () -> {
			UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("password", "newpassword", "newpassword");
			userService.updatePassword(1, updateUserPasswordRequest);
		});
	}
}
