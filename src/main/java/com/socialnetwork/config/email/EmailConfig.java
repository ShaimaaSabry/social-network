package com.socialnetwork.config.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Getter
@Setter
public class EmailConfig {
    private VerificationEmail verificationEmail;
}
