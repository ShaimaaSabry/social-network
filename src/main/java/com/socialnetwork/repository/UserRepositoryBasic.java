package com.socialnetwork.repository;

import com.socialnetwork.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositoryBasic extends CrudRepository<UserEntity, Long> {

    Collection<UserEntity> findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(String firstName, String lastName, String email);

    Optional<UserEntity> findFirstByIdAndPasswordHash(long id, String passwordHash);

    Optional<UserEntity> findFirstByEmail(String email);
}
