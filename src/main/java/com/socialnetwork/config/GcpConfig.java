package com.socialnetwork.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gcp")
@Getter
@Setter
public class GcpConfig {
    private String profilePictureBucket;
    private String gsBaseUrl;
    private String gsBasePath;
}
