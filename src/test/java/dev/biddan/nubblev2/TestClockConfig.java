package dev.biddan.nubblev2;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestClockConfig {

    @Bean("systemClock")
    public FixableClock testableSystemClock() {
        return new FixableClock();
    }
}
