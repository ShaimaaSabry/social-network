package com.socialnetwork.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtConfig {
	private String secretKey;
	private String issuer;
	private long accessTokenTtlInMinutes;
	private long refreshTokenTtlInMinutes;
}
