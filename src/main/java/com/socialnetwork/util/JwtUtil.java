package com.socialnetwork.util;

import java.util.Optional;

import io.jsonwebtoken.Claims;

public interface JwtUtil {
	String generateAccessToken(String subject);
	String generateRefreshToken(String subject);
	Optional<Claims> getClaims(String token);
	Optional<String> getSubject(String token);
}
