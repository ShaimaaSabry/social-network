package com.socialnetwork.util;

import com.socialnetwork.domain.outport.EmailVerificationService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final String KEY_PREFIX = "EMAIL_VERIFICATION_CODE_";
    private static final int TEN_MINUTES_IN_SECONDS = 10 * 60;
    private final JedisPool jedisPool;

    public EmailVerificationServiceImpl(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String generateVerificationCode(String userId) {
        String verificationCode = UUID.randomUUID().toString();

        String key = KEY_PREFIX + userId;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, verificationCode);
            jedis.expire(key, TEN_MINUTES_IN_SECONDS);
        }

        return verificationCode;
    }

    public boolean verify(String userId, String verificationCode) {
        String key = KEY_PREFIX + userId;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            return verificationCode.equals(value);
        }
    }
}
