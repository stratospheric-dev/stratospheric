package dev.stratospheric.cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

import static java.util.Objects.requireNonNull;

public class StratosphericCognitoApp {

  public static void main(final String[] args) {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    requireNonNull(environmentName, "context variable 'environmentName' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    requireNonNull(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonNull(region, "context variable 'region' must not be null");

    String authName = (String) app.getNode().tryGetContext("authName");
    requireNonNull(authName, "context variable 'authName' must not be null");

    String externalUrl = (String) app.getNode().tryGetContext("externalUrl");
    requireNonNull(externalUrl, "context variable 'externalUrl' must not be null");

    String loginPageDomainPrefix = (String) app.getNode().tryGetContext("loginPageDomainPrefix");
    requireNonNull(externalUrl, "context variable 'loginPageDomainPrefix' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    new StratosphericCognitoStack(app, "cognito", awsEnvironment, applicationEnvironment, new StratosphericCognitoStack.CognitoInputParameters(
      authName,
      externalUrl,
      loginPageDomainPrefix));

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
