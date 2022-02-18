package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.domain.outport.EmailVerificationService;
import com.socialnetwork.repository.UserEntity;
import com.socialnetwork.rest.dto.CreateUserRequest;
import com.socialnetwork.rest.dto.UpdateUserPasswordRequest;
import com.socialnetwork.rest.dto.UpdateUserRequest;
import com.socialnetwork.rest.dto.VerifyUserEmailRequest;
import com.socialnetwork.domain.outport.EmailService;
import com.socialnetwork.domain.outport.PhotoVerificationService;
import com.socialnetwork.domain.outport.StorageService;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
class UserServiceTest {
    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private StorageService profilePictureStorageService;

    @MockBean
    private PhotoVerificationService photoVerificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Nested
    class GetAll {

        @Test
        void whenSearchQueryMatchesEmail_thenReturnOnlyMatchingUsersList() {
            // GIVEN
            UserEntity user1 = new UserEntity();
            user1.setEmail("user1@example.com");
            entityManager.persist(user1);
            UserEntity user2 = new UserEntity();
            user2.setEmail("user2@example.com");
            entityManager.persist(user2);

            // WHEN
            Set<User> matchingUsers = userService.getAll("user1");

            // THEN
            Assertions.assertEquals(1, matchingUsers.size());
            MatcherAssert.assertThat(matchingUsers, everyItem(hasProperty("id", is(user1.getId()))));
        }

        @Test
        void whenSearchQueryMatchesFirstName_thenReturnOnlyMatchingUsersList() {
            //GIVEN
            UserEntity user1 = new UserEntity();
            user1.setFirstName("user1");
            entityManager.persist(user1);
            UserEntity user2 = new UserEntity();
            user2.setFirstName("user2");
            entityManager.persist(user2);

            // WHEN
            Set<User> matchingUsers = userService.getAll("user1");

            // THEN
            Assertions.assertEquals(1, matchingUsers.size());
            MatcherAssert.assertThat(matchingUsers, everyItem(hasProperty("id", is(user1.getId()))));
        }

        @Test
        void whenSearchQueryMatchesLastName_thenReturnOnlyMatchingUsersList() {
            // GIVEN
            UserEntity user1 = new UserEntity();
            user1.setLastName("user1");
            entityManager.persist(user1);
            UserEntity user2 = new UserEntity();
            user2.setLastName("user2");
            entityManager.persist(user2);

            // WHEN
            Set<User> matchingUsers = userService.getAll("user1");

            // THEN
            Assertions.assertEquals(1, matchingUsers.size());
            MatcherAssert.assertThat(matchingUsers, everyItem(hasProperty("id", is(user1.getId()))));
        }
    }

    @Nested
    class Create {

        @Test
        void whenCreateUserRequestIsValid_thenCreateUser() throws MessagingException {
            // WHEN
            CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1", "user1@example.com", "password", "password");
            User user = userService.create(createUserRequest);

            // THEN
            UserEntity createdUser = entityManager.find(UserEntity.class, user.getId());
            Assertions.assertEquals(createUserRequest.getFirstName(), createdUser.getFirstName());
            Assertions.assertEquals(createUserRequest.getLastName(), createdUser.getLastName());
            Assertions.assertEquals(createUserRequest.getEmail(), createdUser.getEmail());
            Assertions.assertFalse(createdUser.isEmailVerified());
            Assertions.assertTrue(bCryptPasswordEncoder.matches(createUserRequest.getPassword(), createdUser.getPasswordHash()));
        }
    }

    @Nested
    class VerifyEmail {

        @Test
        void whenVerificationCodeIsValid_thenSetEmailVerifiedToTrue() throws UserNotFoundException {
            // GIVEN
            UserEntity user = new UserEntity();
            entityManager.persist(user);

            Mockito.doReturn(true).when(emailVerificationService).verify(user.getId().toString(), "token");

            // WHEN
            boolean result = userService.verifyEmail(user.getId(), "token");

            // THEN
            Assertions.assertTrue(result);
            user = entityManager.find(UserEntity.class, user.getId());
            Assertions.assertTrue(user.isEmailVerified());
        }

        @Test
        void whenVerificationCodeIsInvalid_thenReturnFalse() throws UserNotFoundException {
            // WHEN
            boolean result = userService.verifyEmail(1L, "invalid token");

            // THEN
            Assertions.assertFalse(result);
        }
    }

    @Nested
    class Update {

        @Test
        void whenUpdateUserRequestIsValid_thenUpdateFirstNameAndLastName() throws UserNotFoundException {
            // GIVEN
            UserEntity user = new UserEntity();
            entityManager.persist(user);

            // WHEN
            UpdateUserRequest updateUserRequest = new UpdateUserRequest("updated first name", "updated last name");
            userService.update(user.getId(), updateUserRequest);

            // THEN
            user = entityManager.find(UserEntity.class, user.getId());
            Assertions.assertEquals(updateUserRequest.getFirstName(), user.getFirstName());
            Assertions.assertEquals(updateUserRequest.getLastName(), user.getLastName());
        }

        @Test
        void whenUserDoesNotExist_thenThrowUserNotFoundException() {
            Assertions.assertThrows(UserNotFoundException.class, () -> {
                UpdateUserRequest updateUserRequest = new UpdateUserRequest("updated first name", "updated last name");
                userService.update(1, updateUserRequest);
            });
        }
    }

    @Nested
    class UpdatePassword {

        @Test
        void whenUpdatePasswordRequestIsValid_thenUpdatePassword() throws UserNotFoundException {
            // GIVEN
            UserEntity user = new UserEntity();
            String passwordHash = bCryptPasswordEncoder.encode("password");
            user.setPasswordHash(passwordHash);
            entityManager.persist(user);

            // WHEN
            UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("password", "newpassword", "newpassword");
            userService.updatePassword(user.getId(), updateUserPasswordRequest);

            // THEN
            user = entityManager.find(UserEntity.class, user.getId());
            Assertions.assertTrue(bCryptPasswordEncoder.matches(updateUserPasswordRequest.getNewPassword(), user.getPasswordHash()));
        }

        @Test
        void whenCurrentPasswordIsInvalid_thenDoNotUpdatePassword() {
            // GIVEN
            final UserEntity user = new UserEntity();
            String passwordHash = bCryptPasswordEncoder.encode("password");
            user.setPasswordHash(passwordHash);
            entityManager.persist(user);

            // WHEN
            UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("incorrect password", "newpassword", "newpassword");
            Assertions.assertThrows(UserNotFoundException.class, () -> {
                userService.updatePassword(user.getId(), updateUserPasswordRequest);
            });

            // THEN
            UserEntity updatedUser = entityManager.find(UserEntity.class, user.getId());
            Assertions.assertTrue(bCryptPasswordEncoder.matches("password", updatedUser.getPasswordHash()));
        }

        @Test
        void whenUserDoesNotExist_thenThrowUserNotFoundException() {
            Assertions.assertThrows(UserNotFoundException.class, () -> {
                UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest("password", "newpassword", "newpassword");
                userService.updatePassword(1, updateUserPasswordRequest);
            });
        }
    }
}