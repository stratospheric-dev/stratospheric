package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.DockerRepository;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class DockerRepositoryApp {

  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Validations.requireNonEmpty(region, "context variable 'region' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    Validations.requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

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
