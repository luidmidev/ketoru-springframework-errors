package com.ketoru.springframework.errors;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Log4j2
@Configuration
public class ErrorsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static ReloadableResourceBundleMessageSource messageSource() {
        log.info("Configuring message source for i18n");
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:lang/error-messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
