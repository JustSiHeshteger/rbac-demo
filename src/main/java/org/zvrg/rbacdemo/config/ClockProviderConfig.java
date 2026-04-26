package org.zvrg.rbacdemo.config;

import jakarta.validation.ClockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockProviderConfig {

    @Bean
    public ClockProvider clockProvider() {
        return Clock::systemDefaultZone;
    }

}
