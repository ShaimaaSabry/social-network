package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.rest.dto.CreateUserRequest;
import com.socialnetwork.rest.dto.UpdateUserPasswordRequest;
import com.socialnetwork.rest.dto.UpdateUserRequest;
import com.socialnetwork.rest.dto.VerifyUserEmailRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {
    Set<User> getAll(String q);

    User create(CreateUserRequest createUserRequest) throws MessagingException;

    User update(long userId, UpdateUserRequest updateUserRequest) throws UserNotFoundException;

    void sendVerificationEmail(long userId) throws MessagingException, UserNotFoundException;

    boolean verifyEmail(long userId, String verificationCode) throws UserNotFoundException;

    boolean updatePassword(long userId, UpdateUserPasswordRequest updateUserPasswordRequest) throws UserNotFoundException;


    User updateProfilePicture(long userId, MultipartFile profilePicture) throws UserNotFoundException, IOException;

    User deleteProfilePicture(long userId) throws UserNotFoundException;
}
