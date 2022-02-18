package com.socialnetwork.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
