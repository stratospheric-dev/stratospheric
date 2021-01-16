package dev.stratospheric.cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import java.util.Objects;

public class StratosphericDockerRepositoryApp {

  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Objects.requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Objects.requireNonNull(region, "context variable 'region' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    Objects.requireNonNull(applicationName, "context variable 'applicationName' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    Stack dockerRepositoryStack = new Stack(app, "DockerRepositoryStack", StackProps.builder()
      .stackName(applicationName + "-DockerRepository")
      .env(awsEnvironment)
      .build());

    DockerRepository dockerRepository = new DockerRepository(
      dockerRepositoryStack,
      "DockerRepository",
      awsEnvironment,
      new DockerRepository.DockerRepositoryInputParameters(applicationName, accountId));

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
