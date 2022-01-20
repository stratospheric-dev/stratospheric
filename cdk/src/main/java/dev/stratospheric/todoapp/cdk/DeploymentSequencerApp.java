package dev.stratospheric.todoapp.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

import static dev.stratospheric.todoapp.cdk.Validations.requireNonEmpty;

public class DeploymentSequencerApp {

  public static void main(final String[] args) {
    App app = new App();

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonEmpty(region, "context variable 'region' must not be null");

    String githubToken = (String) app.getNode().tryGetContext("githubToken");
    requireNonEmpty(githubToken, "context variable 'githubToken' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    new DeploymentSequencerStack(
      app,
      "sequencerStack",
      awsEnvironment,
      applicationName,
      githubToken);

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
