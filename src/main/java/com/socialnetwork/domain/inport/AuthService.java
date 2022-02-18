package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.InvalidUserCredentialsException;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.rest.dto.AuthRequest;
import com.socialnetwork.rest.dto.AuthResponse;
import com.socialnetwork.rest.dto.RefreshTokenRequest;

public interface AuthService {
    User authenticate(String email, String password) throws InvalidUserCredentialsException;

    User findOneById(Long userId) throws UserNotFoundException;
}
