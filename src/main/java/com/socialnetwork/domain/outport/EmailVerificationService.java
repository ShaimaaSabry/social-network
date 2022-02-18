package com.socialnetwork.domain.outport;

public interface EmailVerificationService {

    String generateVerificationCode(String userId);

    boolean verify(String userId, String verificationCode);
}
