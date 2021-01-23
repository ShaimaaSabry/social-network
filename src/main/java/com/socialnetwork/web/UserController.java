package com.socialnetwork.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.socialnetwork.dto.CreateUserRequest;
import com.socialnetwork.dto.UpdateUserPasswordRequest;
import com.socialnetwork.dto.UpdateUserRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.dto.VerifyUserEmailRequest;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
@Api(tags = "Users")
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping
	@ApiOperation(value = "Search Users", notes = "Search existing users by first name, last name or email.")
	public List<UserResponse> getAll(@RequestParam(required = true) String q) {
		return userService.getAll(q);
	}

	@PostMapping
	@ApiOperation(value = "Create User", notes = "Signup to the social network app.")
	public ResponseEntity<UserResponse> create(@Validated @RequestBody CreateUserRequest createUserRequest) throws AddressException, MessagingException {
		UserResponse userResponse = userService.create(createUserRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}")
				.buildAndExpand(userResponse.getId()).toUri();
		return ResponseEntity.created(location).body(userResponse);
	}
	
	@PutMapping("account/sendverificationemail")
	@ApiOperation(value = "Send Verification Email", notes = "Resend verification email.")
	public void sendVerificationEmail(Authentication authentication) throws AddressException, InvalidUserIdException, MessagingException {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		userService.sendVerificationEmail(userId);
	}
	
	@PutMapping("account/verifyemail")
	@ApiOperation(value = "Verify User Email", notes = "Verify your email.")
	public ResponseEntity<Void> verifyEmail(@Validated @RequestBody VerifyUserEmailRequest verfiyUserEmailRequest) {
		if(userService.verifyEmail(verfiyUserEmailRequest)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("account")
	@ApiOperation(value = "Update User", notes = "Update your first name or last name.")
	public UserResponse update(Authentication authentication,
			@Validated @RequestBody UpdateUserRequest updateUserRequest) throws InvalidUserIdException {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		return userService.update(userId, updateUserRequest);
	}
	
	@PutMapping("account/profilepicture")
	@ApiOperation(value = "Update User Profile Picture", notes = "Update your profile picture.")
	public UserResponse updateProfilePicture(Authentication authentication,
			@RequestParam(required = true) MultipartFile profilePicture) throws InvalidUserIdException, FileNotFoundException, IOException {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		return userService.updateProfilePicture(userId, profilePicture);
	}
	
	@DeleteMapping("account/profilepicture")
	@ApiOperation(value = "Delete User Profile Picture", notes = "Delete your profile picture.")
	public UserResponse updateProfilePicture(Authentication authentication) throws InvalidUserIdException {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		return userService.deleteProfilePicture(userId);
	}

	@PutMapping("account/password")
	@ApiOperation(value = "Update User Password", notes = "Update your password.")
	public ResponseEntity<Void> updatePassword(Authentication authentication,
			@Validated @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) throws InvalidUserIdException {
		Long userId = Long.parseLong(authentication.getPrincipal().toString());
		if(userService.updatePassword(userId, updateUserPasswordRequest)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
