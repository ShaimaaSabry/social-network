package com.socialnetwork.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socialnetwork.domain.EmailVerificationToken;

@Repository
public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long>{
	Optional<EmailVerificationToken> findFirstByToken(String emailVerificationToken);
}
