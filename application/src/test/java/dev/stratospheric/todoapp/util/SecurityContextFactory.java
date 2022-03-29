package dev.stratospheric.todoapp.util;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;

public class SecurityContextFactory {

  public static void createSecurityContext(String emailAddress) {
    SecurityContextHolder.setContext(
      SecurityContextHolder.createEmptyContext()
    );

    OidcUser user = new DefaultOidcUser(
      null,
      new OidcIdToken(
        emailAddress,
        Instant.now(),
        Instant.MAX,
        Map.of(
          "email", emailAddress,
          "sub", emailAddress,
          "name", emailAddress
        )
      )
    );

    SecurityContextHolder.getContext().setAuthentication(
      new TestingAuthenticationToken(user, null)
    );
  }
}
