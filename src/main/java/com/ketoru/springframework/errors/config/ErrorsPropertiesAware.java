package com.ketoru.springframework.errors.config;

import org.springframework.beans.factory.Aware;

public interface ErrorsPropertiesAware extends Aware {
    void setErrorsConfiguration(ErrorsProperties properties);
}
