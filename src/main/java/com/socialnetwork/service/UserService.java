package com.socialnetwork.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.web.multipart.MultipartFile;

import com.socialnetwork.dto.CreateUserRequest;
import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.UpdateUserPasswordRequest;
import com.socialnetwork.dto.UpdateUserRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.dto.VerifyUserEmailRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;

public interface UserService {
	List<UserResponse> getAll(String q);
	UserResponse getOneById(long userId) throws InvalidUserIdException;
	UserResponse getOneByEmailAndPassword(AuthRequest authRequest) throws InvalidUserCredentialsException;
	UserResponse create(CreateUserRequest createUserRequest) throws AddressException, MessagingException;
	void sendVerificationEmail(long userId) throws InvalidUserIdException, AddressException, MessagingException;
	boolean verifyEmail(VerifyUserEmailRequest verifyUserEmailRequest);
	UserResponse update(long userId, UpdateUserRequest updateUserRequest) throws InvalidUserIdException;
	UserResponse updateProfilePicture(long userId, MultipartFile profilePicture)
			throws InvalidUserIdException, FileNotFoundException, IOException;
	UserResponse deleteProfilePicture(long userId) throws InvalidUserIdException;
	boolean updatePassword(long userId, UpdateUserPasswordRequest updateUserPasswordRequest) throws InvalidUserIdException;
}
