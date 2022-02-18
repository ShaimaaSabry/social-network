package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.InvalidUserCredentialsException;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.domain.UserNotFoundException;
import com.socialnetwork.repository.UserEntity;
import com.socialnetwork.rest.dto.AuthRequest;
import com.socialnetwork.rest.dto.AuthResponse;
import com.socialnetwork.rest.dto.RefreshTokenRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Nested
    class Authenticate {

        @Test
        void whenEmailAndPasswordAreValid_thenReturnUser() throws InvalidUserCredentialsException {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail("user1@example.com");
            String passwordHash = bCryptPasswordEncoder.encode("password");
            userEntity.setPasswordHash(passwordHash);
            entityManager.persist(userEntity);

            // WHEN
            User user = authService.authenticate("user1@example.com", "password");

            // THEN
            MatcherAssert.assertThat(user, is(not(nullValue())));
            MatcherAssert.assertThat(user.getId(), is(userEntity.getId()));
        }

        @Test
        void whenEmailIsInvalid_thenThrowInvalidUserCredentialsException() {
            // GIVEN
            UserEntity user = new UserEntity();
            user.setEmail("user1@example.com");
            String passwordHash = bCryptPasswordEncoder.encode("password");
            user.setPasswordHash(passwordHash);
            entityManager.persist(user);

            // WHEN THEN
            Assertions.assertThrows(InvalidUserCredentialsException.class, () -> {
                authService.authenticate("invalid@example.com", "password");
            });
        }

        @Test
        void whenPasswordIsInvalid_thenThrowInvalidUserCredentialsException() {
            // GIVEN
            UserEntity user = new UserEntity();
            user.setEmail("user1@example.com");
            String passwordHash = bCryptPasswordEncoder.encode("password");
            user.setPasswordHash(passwordHash);
            entityManager.persist(user);

            // WHEN THEN
            Assertions.assertThrows(
                    InvalidUserCredentialsException.class,
                    () -> authService.authenticate("user1@example.com", "invalid"));
        }
    }

    @Nested
    class FindOneById {

        @Test
        void whenUserExists_thenReturnUser() throws UserNotFoundException {
            // GIVEN
            UserEntity userEntity = new UserEntity();
            entityManager.persist(userEntity);

            // WHEN
            User user = authService.findOneById(userEntity.getId());

            // THEN
            MatcherAssert.assertThat(user, is(not(nullValue())));
            MatcherAssert.assertThat(user.getId(), is(userEntity.getId()));
        }

        @Test
        void whenUserDoesNotExist_thenThrowUserNotFoundException() {
            Assertions.assertThrows(
                    UserNotFoundException.class,
                    () -> authService.findOneById(1L));
        }
    }
}
