package com.socialnetwork.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "mail.smtp")
@Getter
@Setter
public class MailConfig {
	private String host;
	private String port;
	private String user;
	private String password;
}
