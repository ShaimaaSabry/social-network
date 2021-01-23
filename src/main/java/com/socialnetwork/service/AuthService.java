package com.socialnetwork.service;

import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.AuthResponse;
import com.socialnetwork.dto.RefreshTokenRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;

public interface AuthService {
	AuthResponse authenticate(AuthRequest authRequest) throws InvalidUserCredentialsException;
	AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws InvalidUserCredentialsException, InvalidUserIdException;
}
