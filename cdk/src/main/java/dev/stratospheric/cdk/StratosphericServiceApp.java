package dev.stratospheric.cdk;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

public class StratosphericServiceApp {

  public static void main(final String[] args) {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    requireNonNull(environmentName, "context variable 'environmentName' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    requireNonNull(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonNull(accountId, "context variable 'accountId' must not be null");

    String springProfile = (String) app.getNode().tryGetContext("springProfile");
    requireNonNull(springProfile, "context variable 'springProfile' must not be null");

    String dockerRepositoryName = (String) app.getNode().tryGetContext("dockerRepositoryName");
    requireNonNull(dockerRepositoryName, "context variable 'dockerRepositoryName' must not be null");

    String dockerImageTag = (String) app.getNode().tryGetContext("dockerImageTag");
    requireNonNull(dockerImageTag, "context variable 'dockerImageTag' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonNull(region, "context variable 'region' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    Stack serviceStack = new Stack(app, "ServiceStack", StackProps.builder()
      .stackName(environmentName + "-Service")
      .env(awsEnvironment)
      .build());

    Service service = new Service(
      serviceStack,
      "Service",
      awsEnvironment,
      applicationEnvironment,
      new Service.ServiceInputParameters(
        new Service.DockerImageSource(dockerRepositoryName, dockerImageTag),
        emptyList(),
        environmentVariables(
          serviceStack,
          applicationEnvironment,
          springProfile)),
      Network.getOutputParametersFromParameterStore(serviceStack, applicationEnvironment.getEnvironmentName()));

    app.synth();
  }

  static Map<String, String> environmentVariables(
    Construct scope,
    ApplicationEnvironment applicationEnvironment,
    String springProfile
  ) {

    Map<String, String> vars = new HashMap<>();

    PostgresDatabase.DatabaseOutputParameters databaseOutputParameters =
      PostgresDatabase.getOutputParametersFromParameterStore(scope, applicationEnvironment);
    String databaseSecretArn = databaseOutputParameters.getDatabaseSecretArn();
    ISecret databaseSecret = Secret.fromSecretCompleteArn(scope, "databaseSecret", databaseSecretArn);

    StratosphericCognitoStack.CognitoOutputParameters cognitoOutputParameters =
      StratosphericCognitoStack.getOutputParametersFromParameterStore(scope, applicationEnvironment);

    StratosphericMessagingStack.MessagingOutputParameters messagingOutputParameters =
      StratosphericMessagingStack.getOutputParametersFromParameterStore(scope, applicationEnvironment);

    vars.put("SPRING_PROFILES_ACTIVE", springProfile);
    vars.put("SPRING_DATASOURCE_URL",
      String.format("jdbc:postgresql://%s:%s/%s",
        databaseOutputParameters.getEndpointAddress(),
        databaseOutputParameters.getEndpointPort(),
        databaseOutputParameters.getDbName()));
    vars.put("SPRING_DATASOURCE_USERNAME",
      databaseSecret.secretValueFromJson("username").toString());
    vars.put("SPRING_DATASOURCE_PASSWORD",
      databaseSecret.secretValueFromJson("password").toString());
    vars.put("COGNITO_CLIENT_ID", cognitoOutputParameters.getUserPoolClientId());
    vars.put("COGNITO_CLIENT_SECRET", cognitoOutputParameters.getUserPoolClientSecret());
    vars.put("COGNITO_USER_POOL_ID", cognitoOutputParameters.getUserPoolId());
    vars.put("TODO_SHARING_QUEUE_NAME", messagingOutputParameters.getTodoSharingQueueName());
    vars.put("TODO_UPDATES_TOPIC_NAME", messagingOutputParameters.getTodoUpdatesTopicName());

    return vars;
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
