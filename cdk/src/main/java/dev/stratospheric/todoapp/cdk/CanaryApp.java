package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

import java.io.IOException;

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

    String username = (String) app.getNode().tryGetContext("username");
    Validations.requireNonEmpty(username, "context variable 'username' must not be null");

    String password = (String) app.getNode().tryGetContext("password");
    Validations.requireNonEmpty(password, "context variable 'password' must not be null");

    String targetUrl = (String) app.getNode().tryGetContext("targetUrl");
    Validations.requireNonEmpty(targetUrl, "context variable 'targetUrl' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    new CanaryStack(app, "canary", awsEnvironment, applicationEnvironment, targetUrl, username, password);

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
