package com.socialnetwork.service;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.AuthResponse;
import com.socialnetwork.dto.RefreshTokenRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.util.JwtUtil;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class AuthServiceTest {
	@Autowired
	private AuthService authService;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Test
	public void whenEmailAndPasswordAreValid_thenReturnAccessTokenAndRefreshToken() throws InvalidUserCredentialsException {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);

		AuthRequest authRequest = new AuthRequest("user1@example.com", "password");
		AuthResponse authResponse = authService.authenticate(authRequest);
		
		Assertions.assertEquals(user.getId(), authResponse.getUser().getId());
		Assertions.assertEquals(user.getId(), Long.parseLong(jwtUtil.getSubject(authResponse.getAccessToken()).get()));
		Assertions.assertEquals(user.getId(), Long.parseLong(jwtUtil.getSubject(authResponse.getRefreshToken()).get()));
	}
	
	@Test
	public void whenEmailIsInvalid_thenThrowInvalidUserCredentialsException() {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);

		Assertions.assertThrows(InvalidUserCredentialsException.class, () -> {
			AuthRequest authRequest = new AuthRequest("incorrect@example.com", "password");
			authService.authenticate(authRequest);
		});
	}
	
	@Test
	public void whenPasswordIsInvalid_thenThrowInvalidUserCredentialsException() {
		String passwordHash = bCryptPasswordEncoder.encode("password");
		User user = new UserBuilder("user1@example.com").passwordHash(passwordHash).build();
		entityManager.persist(user);

		Assertions.assertThrows(InvalidUserCredentialsException.class, () -> {
			AuthRequest authRequest = new AuthRequest("user@example.com", "incorrect");
			authService.authenticate(authRequest);
		});
	}
	
	@Test
	public void whenRefreshTokenIsValid_thenReturnAccessTokenAndRefreshToken() throws InvalidUserCredentialsException, InvalidUserIdException {
		User user = new UserBuilder("user1@example.com").build();
		entityManager.persist(user);
		String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
		AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
		
		Assertions.assertEquals(user.getId(), authResponse.getUser().getId());
		Assertions.assertEquals(user.getId(), Long.parseLong(jwtUtil.getSubject(authResponse.getAccessToken()).get()));
		Assertions.assertEquals(user.getId(), Long.parseLong(jwtUtil.getSubject(authResponse.getRefreshToken()).get()));
	}
}
