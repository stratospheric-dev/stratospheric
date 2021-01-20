package dev.stratospheric;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

public class Chapter13App {
  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonEmpty(region, "context variable 'region' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      "stratospheric",
      "dev"
    );

    new ActiveMqStack(app, "activeMq", awsEnvironment, applicationEnvironment, "stratospheric");

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

  static void requireNonEmpty(String string, String message) {
    if (string == null || string.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }
}
