package com.socialnetwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socialnetwork.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	List<User> findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(String firstName, String lastName, String email);
	Optional<User> findFirstByEmail(String email);
}
