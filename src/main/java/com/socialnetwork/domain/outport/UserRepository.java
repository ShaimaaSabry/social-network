package com.socialnetwork.domain.outport;

import com.socialnetwork.domain.InvalidUserCredentialsException;
import com.socialnetwork.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    Set<User> search(String q);
    Optional<User> findById(Long id);
    Optional<User> findByIdAndPassword(long id, String password);
    Optional<User> findFirstByEmailAndPassword(String email, String password) throws InvalidUserCredentialsException;
    User save(User user);
}
