package dev.stratospheric.cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import static java.util.Objects.requireNonNull;

public class StratosphericNetworkApp {

  public static void main(final String[] args) {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    requireNonNull(environmentName, "context variable 'environmentName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonNull(region, "context variable 'region' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    Stack networkStack = new Stack(app, "NetworkStack", StackProps.builder()
      .stackName(environmentName + "-Network")
      .env(awsEnvironment)
      .build());

    Network network = new Network(
      networkStack,
      "Network",
      awsEnvironment,
      environmentName,
      new Network.NetworkInputParameters());

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
