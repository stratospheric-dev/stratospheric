package dev.aws101.config;

import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.context.annotation.ConditionalOnAwsCloudEnvironment;
import org.springframework.cloud.aws.context.annotation.ConditionalOnMissingAwsCloudEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

  private final PersonRepository personRepository;

  @Autowired
  public JpaAuditingConfiguration(
    PersonRepository personRepository
  ) {
    this.personRepository = personRepository;
  }

  @Bean
  @ConditionalOnAwsCloudEnvironment
  public AuditorAware<Person> auditorProvider() {
    return () -> personRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Bean
  @ConditionalOnMissingAwsCloudEnvironment
  public AuditorAware<Person> fakeAuditorProvider() {
    return () -> personRepository.findByName("Admin");
  }
}
