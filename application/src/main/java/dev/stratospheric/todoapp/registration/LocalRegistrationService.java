package dev.stratospheric.todoapp.registration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class LocalRegistrationService implements RegistrationService {

  @Override
  public void registerUser(Registration registration) {
    // no registration as we use a local Keycloak instance with a pre-defined set of users
  }
}
