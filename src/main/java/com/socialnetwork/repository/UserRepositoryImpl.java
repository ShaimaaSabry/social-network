package com.socialnetwork.repository;

import com.socialnetwork.domain.InvalidUserCredentialsException;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.outport.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class UserRepositoryImpl implements UserRepository {
    private final UserRepositoryBasic userRepository;
    private final UserEntityMapper mapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    UserRepositoryImpl(UserRepositoryBasic userRepository, UserEntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    public Set<User> search(String q) {
        Collection<UserEntity> users = userRepository
                .findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(q, q, q);

        return  users.stream()
                .map(mapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);

        if (!user.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(mapper.map(user.get()));
    }

    @Override
    public Optional<User> findByIdAndPassword(long id, String password) {
        Optional<UserEntity> user = userRepository.findById(id);
//        String passwordHash = bCryptPasswordEncoder.encode(password);
//        Optional<UserEntity> user = userRepository.findFirstByIdAndPasswordHash(id, passwordHash);

        if (!user.isPresent()) {
            return Optional.empty();
        }

        boolean passwordMatches = bCryptPasswordEncoder.matches(password, user.get().getPasswordHash());
        if (!passwordMatches) {
            return Optional.empty();
        }

        return Optional.of(mapper.map(user.get()));
    }

    @Override
    public Optional<User> findFirstByEmailAndPassword(String email, String password) throws InvalidUserCredentialsException {
        Optional<UserEntity> user = userRepository.findFirstByEmail(email);
        if (!user.isPresent()) {
            throw new InvalidUserCredentialsException();
        }

        boolean passwordMatches = bCryptPasswordEncoder.matches(password, user.get().getPasswordHash());
        if (!passwordMatches) {
            throw new InvalidUserCredentialsException();
        }

        return Optional.of(mapper.map(user.get()));
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = map(user);

        userRepository.save(userEntity);

        return mapper.map(userEntity);
    }

    private UserEntity map(User user) {
        UserEntity userEntity = mapper.map(user);

        if (user.getPassword() != null) {
            String passwordHash = bCryptPasswordEncoder.encode(user.getPassword());
            userEntity.setPasswordHash(passwordHash);
        }

        return userEntity;
    }
}
