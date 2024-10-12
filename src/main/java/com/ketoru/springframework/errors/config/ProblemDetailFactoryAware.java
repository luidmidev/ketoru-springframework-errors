package com.ketoru.springframework.errors.config;

import com.ketoru.springframework.errors.ProblemDetailFactory;
import org.springframework.beans.factory.Aware;

public interface ProblemDetailFactoryAware extends Aware {
    void setProblemDetailFactory(ProblemDetailFactory problemDetailFactory);
}
