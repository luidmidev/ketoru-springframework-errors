package com.ketoru.springframework.errors.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.ketoru.springframework.errors")
public class ErrorsProperties {
    private boolean allErrors = false;
    private boolean logErrors = false;
}
