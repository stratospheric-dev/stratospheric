package dev.stratospheric.todoapp.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Required to use Micrometer's @Timed annotation on any arbitrary method
 * See <a href="https://micrometer.io/docs/concepts#_the_timed_annotation">...</a>
 */
@Configuration
public class TimedConfiguration {

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }
}
