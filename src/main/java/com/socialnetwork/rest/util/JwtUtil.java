package com.socialnetwork.rest.util;

import com.socialnetwork.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    public String generateAccessToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        return generateAccessToken(subject, claims);
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey());
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(jwtConfig.getAccessTokenTtlInMinutes()).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    public String generateRefreshToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        return generateRefreshToken(subject, claims);
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey());
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(jwtConfig.getRefreshTokenTtlInMinutes()).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    public Optional<Claims> getClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey()))
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.of(claims);
        } catch (ExpiredJwtException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> getSubject(String token) {
        Optional<Claims> claims = getClaims(token);
        if (!claims.isPresent())
            return Optional.empty();

        String subject = claims.get().getSubject();
        return Optional.of(subject);
    }
}
