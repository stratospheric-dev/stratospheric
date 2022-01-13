package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Network.NetworkInputParameters;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class NetworkApp {

  public static void main(final String[] args) {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    Validations.requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Validations.requireNonEmpty(region, "context variable 'region' must not be null");

    String sslCertificateArn = (String) app.getNode().tryGetContext("sslCertificateArn");

    Environment awsEnvironment = makeEnv(accountId, region);

    Stack networkStack = new Stack(app, "NetworkStack", StackProps.builder()
      .stackName(environmentName + "-Network")
      .env(awsEnvironment)
      .build());

    NetworkInputParameters inputParameters = new NetworkInputParameters();

    if(sslCertificateArn != null && !sslCertificateArn.isEmpty()){
      inputParameters.withSslCertificateArn(sslCertificateArn);
    }

    Network network = new Network(
      networkStack,
      "Network",
      awsEnvironment,
      environmentName,
      inputParameters);

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
