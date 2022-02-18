package com.socialnetwork.rest.controller;

import com.socialnetwork.domain.InvalidUserCredentialsException;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.domain.inport.AuthService;
import com.socialnetwork.rest.dto.AuthRequest;
import com.socialnetwork.rest.dto.AuthResponse;
import com.socialnetwork.rest.dto.RefreshTokenRequest;
import com.socialnetwork.rest.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Api(tags = "Authentication")
class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signin")
    @ApiOperation(value = "Signin", notes = "Signin to the social network app.")
    AuthResponse signin(@Validated @RequestBody AuthRequest authRequest) throws InvalidUserCredentialsException {
        User user = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());

        return generateTokens(user);
    }

    @PostMapping("/tokens")
    @ApiOperation(value = "Refresh Token", notes = "Obtain a new access token using your refresh token.")
    AuthResponse refreshToken(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) throws InvalidUserCredentialsException, UserNotFoundException {
        Optional<String> userId = jwtUtil.getSubject(refreshTokenRequest.getRefreshToken());
        if (!userId.isPresent()) {
            throw new InvalidUserCredentialsException();
        }

        User user = authService.findOneById(Long.parseLong(userId.get()));

        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {
        final String accessToken = jwtUtil.generateAccessToken(Long.toString(user.getId()));
        final String refreshToken = jwtUtil.generateRefreshToken(Long.toString(user.getId()));
        return new AuthResponse(null, accessToken, refreshToken);
    }
}
