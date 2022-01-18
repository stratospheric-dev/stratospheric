package dev.stratospheric.todoapp.cdk;

import java.io.IOException;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

public class CanaryApp {

  public static void main(final String[] args) throws IOException {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    Validations.requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    Validations.requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Validations.requireNonEmpty(region, "context variable 'region' must not be null");

    String canaryUsername = (String) app.getNode().tryGetContext("canaryUsername");
    Validations.requireNonEmpty(canaryUsername, "context variable 'canaryUsername' must not be null");

    String canaryUserPassword = (String) app.getNode().tryGetContext("canaryUserPassword");
    Validations.requireNonEmpty(canaryUserPassword, "context variable 'canaryUserPassword' must not be null");

    String applicationUrl = (String) app.getNode().tryGetContext("applicationUrl");
    Validations.requireNonEmpty(applicationUrl, "context variable 'applicationUrl' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    new CanaryStack(app, "canary", awsEnvironment, applicationEnvironment, applicationUrl, canaryUsername, canaryUserPassword);

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
