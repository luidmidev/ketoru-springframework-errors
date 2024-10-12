package com.ketoru.springframework.errors.config;

import com.ketoru.springframework.errors.ProblemDetailFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Log4j2
@RequiredArgsConstructor
@Component
public class ErrorBeanPostProcessor implements BeanPostProcessor {

    private final ErrorsProperties properties;
    private final ProblemDetailFactory factory;

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) {

        if (bean instanceof ErrorsPropertiesAware aware) {
            log.info("Setting errors configuration for bean {}", beanName);
            aware.setErrorsConfiguration(properties);
        }

        if (bean instanceof ProblemDetailFactoryAware aware) {
            log.info("Setting problem detail factory for bean {}", beanName);
            aware.setProblemDetailFactory(factory);
        }

        return bean;
    }

}
