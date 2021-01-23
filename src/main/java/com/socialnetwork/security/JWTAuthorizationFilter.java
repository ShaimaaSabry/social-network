package com.socialnetwork.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.socialnetwork.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtil jwtUtil;

	 @Override
	 protected void doFilterInternal(
			 HttpServletRequest request,
			 HttpServletResponse response,
	         FilterChain filterChain) throws IOException, ServletException {
		 String header = request.getHeader("Authorization");
		 if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
		 }
		 
		UsernamePasswordAuthenticationToken authentication = getAuthentication(header.replace("Bearer", ""));
		SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
	}
	 
	 private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		Optional<Claims> claims = jwtUtil.getClaims(token);
		if(!claims.isPresent())
			return null;
		
    	String userId = claims.get().getSubject();
    	
    	UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        return auth;
	 }
}
