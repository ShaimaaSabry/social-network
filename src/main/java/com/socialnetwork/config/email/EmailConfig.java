package com.socialnetwork.config.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "email")
@Getter
@Setter
public class EmailConfig {
	private VerificationEmail verificationEmail;
}
