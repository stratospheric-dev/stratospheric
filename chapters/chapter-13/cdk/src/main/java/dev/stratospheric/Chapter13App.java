package dev.stratospheric;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

import java.util.Objects;

public class Chapter13App {
  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Objects.requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Objects.requireNonNull(region, "context variable 'region' must not be null");

    new ActiveMQStack(app,
      "ActiveMQ",
      makeEnv(accountId, region),
      "stratospheric"
    );

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }
}
