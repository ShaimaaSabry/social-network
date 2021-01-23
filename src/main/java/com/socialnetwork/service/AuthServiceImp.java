package com.socialnetwork.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.AuthResponse;
import com.socialnetwork.dto.RefreshTokenRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.util.JwtUtilImp;

@Service
public class AuthServiceImp implements AuthService {
	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtilImp jwtUtil;

	@Override
	public AuthResponse authenticate(AuthRequest authRequest) throws InvalidUserCredentialsException {
		UserResponse userResponse = userService.getOneByEmailAndPassword(authRequest);

		final String accessToken = jwtUtil.generateAccessToken(Long.toString(userResponse.getId()));
		final String refreshToken = jwtUtil.generateRefreshToken(Long.toString(userResponse.getId()));
		return new AuthResponse(userResponse, accessToken, refreshToken);
	}

	@Override
	public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
			throws InvalidUserCredentialsException, InvalidUserIdException {
		Optional<String> userId = jwtUtil.getSubject(refreshTokenRequest.getRefreshToken());
		if (!userId.isPresent()) {
			throw new InvalidUserCredentialsException();
		}

		UserResponse userResponse = userService.getOneById(Long.parseLong(userId.get()));

		final String accessToken = jwtUtil.generateAccessToken(Long.toString(userResponse.getId()));
		final String refreshToken = jwtUtil.generateRefreshToken(Long.toString(userResponse.getId()));
		return new AuthResponse(userResponse, accessToken, refreshToken);
	}
}
