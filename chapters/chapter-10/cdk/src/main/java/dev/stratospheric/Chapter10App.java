package dev.stratospheric;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chapter10App {
  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Objects.requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Objects.requireNonNull(region, "context variable 'region' must not be null");

    new CognitoStack(app,
      "IdentityProvider",
      makeEnv(accountId, region),
      Map.of(
        "spring-boot-application",
        List.of("http://localhost:8080/login/oauth2/code/cognito", "https://app.stratospheric.dev/login/oauth2/code/cognito")));

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }
}
