package com.socialnetwork.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.AuthResponse;
import com.socialnetwork.dto.RefreshTokenRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/auth")
@Api(tags = "Authentication")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/signin")
	@ApiOperation(value = "Signin", notes = "Signin to the social network app.")
	public AuthResponse signin(@Validated @RequestBody AuthRequest authRequest) throws InvalidUserCredentialsException {
		return authService.authenticate(authRequest);
	}

	@PostMapping("/tokens")
	@ApiOperation(value = "Refresh Token", notes = "Obtain a new access token using your refresh token.")
	public AuthResponse refreshToken(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) throws InvalidUserCredentialsException, InvalidUserIdException {
		return authService.refreshToken(refreshTokenRequest);
		
	}
}
