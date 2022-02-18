package com.socialnetwork.rest.controller;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.domain.inport.UserService;
import com.socialnetwork.rest.dto.CreateUserRequest;
import com.socialnetwork.rest.dto.UpdateUserPasswordRequest;
import com.socialnetwork.rest.dto.UpdateUserRequest;
import com.socialnetwork.rest.dto.UserResponse;
import com.socialnetwork.rest.dto.VerifyUserEmailRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
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

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Api(tags = "Users")
class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Search Users", notes = "Search existing users by first name, last name or email.")
    List<UserResponse> getAll(@RequestParam(required = true) String q) {
        Set<User> userList = userService.getAll(q);

        return userList.stream().map(this::map).collect(Collectors.toList());
    }

    @PostMapping
    @ApiOperation(value = "Create User", notes = "Signup to the social network app.")
    ResponseEntity<UserResponse> create(@Validated @RequestBody CreateUserRequest createUserRequest) throws MessagingException {
        User user = userService.create(createUserRequest);
        UserResponse userResponse = map(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}")
                .buildAndExpand(userResponse.getId()).toUri();
        return ResponseEntity.created(location).body(userResponse);
    }

    @PutMapping("account/email/verification")
    @ApiOperation(value = "Send Verification Email", notes = "Resend verification email.")
    void sendVerificationEmail(Authentication authentication) throws UserNotFoundException, MessagingException {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        userService.sendVerificationEmail(userId);
    }

    @PutMapping("account/email/verify")
    @ApiOperation(value = "Verify User Email", notes = "Verify your email.")
    ResponseEntity<Void> verifyEmail(@Validated @RequestBody VerifyUserEmailRequest verifyUserEmailRequest) throws UserNotFoundException {
        if (userService.verifyEmail(1L, verifyUserEmailRequest.getToken())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("account")
    @ApiOperation(value = "Update User", notes = "Update your first name or last name.")
    UserResponse update(Authentication authentication,
                        @Validated @RequestBody UpdateUserRequest updateUserRequest) throws UserNotFoundException {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        User user = userService.update(userId, updateUserRequest);

        return map(user);
    }

    @PutMapping("account/password")
    @ApiOperation(value = "Update User Password", notes = "Update your password.")
    ResponseEntity<Void> updatePassword(
            Authentication authentication,
            @Validated @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) throws UserNotFoundException {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        if (userService.updatePassword(userId, updateUserPasswordRequest)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("account/profile-picture")
    @ApiOperation(value = "Update User Profile Picture", notes = "Update your profile picture.")
    UserResponse updateProfilePicture(
            Authentication authentication,
            @RequestParam(required = true) MultipartFile profilePicture) throws UserNotFoundException, IOException {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        User user = userService.updateProfilePicture(userId, profilePicture);

        return map(user);
    }

    @DeleteMapping("account/profile-picture")
    @ApiOperation(value = "Delete User Profile Picture", notes = "Delete your profile picture.")
    UserResponse updateProfilePicture(Authentication authentication) throws UserNotFoundException {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        User user = userService.deleteProfilePicture(userId);

        return map(user);
    }

    private UserResponse map(User user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
//		if (user.getProfilePicture() != null) {
//			String profilePictureUrl = gcpConfig.getGsBaseUrl() + user.getProfilePicture().getBucketName() + "/"
//					+ user.getProfilePicture().getBlobName();
//			userResponse.setProfilePictureUrl(profilePictureUrl);
//			userResponse.setProfilePictureValidSelfie(user.getProfilePicture().isValidSelfie());
//		}
        return userResponse;
    }
}
