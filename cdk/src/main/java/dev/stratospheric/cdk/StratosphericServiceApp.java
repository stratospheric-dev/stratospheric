package dev.stratospheric.cdk;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static dev.stratospheric.cdk.Validations.requireNonEmpty;

public class StratosphericServiceApp {

  public static void main(final String[] args) {
    App app = new App();


    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String springProfile = (String) app.getNode().tryGetContext("springProfile");
    requireNonEmpty(springProfile, "context variable 'springProfile' must not be null");

    String dockerRepositoryName = (String) app.getNode().tryGetContext("dockerRepositoryName");
    requireNonEmpty(dockerRepositoryName, "context variable 'dockerRepositoryName' must not be null");

    String dockerImageTag = (String) app.getNode().tryGetContext("dockerImageTag");
    requireNonEmpty(dockerImageTag, "context variable 'dockerImageTag' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonEmpty(region, "context variable 'region' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    // This stack is just a container for the parameters below, because they need a Stack as a scope.
    Stack parametersStack = new Stack(app, "ServiceParameters", StackProps.builder()
      .stackName(applicationEnvironment.prefix("-ServiceParameters"))
      .env(awsEnvironment)
      .build());

    Stack serviceStack = new Stack(app, "ServiceStack", StackProps.builder()
      .stackName(applicationEnvironment.prefix("Service"))
      .env(awsEnvironment)
      .build());

    PostgresDatabase.DatabaseOutputParameters databaseOutputParameters =
      PostgresDatabase.getOutputParametersFromParameterStore(parametersStack, applicationEnvironment);

    StratosphericCognitoStack.CognitoOutputParameters cognitoOutputParameters =
      StratosphericCognitoStack.getOutputParametersFromParameterStore(parametersStack, applicationEnvironment);

    StratosphericMessagingStack.MessagingOutputParameters messagingOutputParameters =
      StratosphericMessagingStack.getOutputParametersFromParameterStore(parametersStack, applicationEnvironment);

    StratosphericActiveMqStack.ActiveMqOutputParameters activeMqOutputParameters =
      StratosphericActiveMqStack.getOutputParametersFromParameterStore(parametersStack, applicationEnvironment);

    Service service = new Service(
      serviceStack,
      "Service",
      awsEnvironment,
      applicationEnvironment,
      new Service.ServiceInputParameters(
        new Service.DockerImageSource(dockerRepositoryName, dockerImageTag),
        Collections.singletonList(databaseOutputParameters.getDatabaseSecurityGroupId()),
        environmentVariables(
          serviceStack,
          databaseOutputParameters,
          cognitoOutputParameters,
          messagingOutputParameters,
          activeMqOutputParameters,
          springProfile)),
      Network.getOutputParametersFromParameterStore(serviceStack, applicationEnvironment.getEnvironmentName()));

    app.synth();
  }

  static Map<String, String> environmentVariables(
    Construct scope,
    PostgresDatabase.DatabaseOutputParameters databaseOutputParameters,
    StratosphericCognitoStack.CognitoOutputParameters cognitoOutputParameters,
    StratosphericMessagingStack.MessagingOutputParameters messagingOutputParameters,
    StratosphericActiveMqStack.ActiveMqOutputParameters activeMqOutputParameters,
    String springProfile
  ) {

    Map<String, String> vars = new HashMap<>();


    String databaseSecretArn = databaseOutputParameters.getDatabaseSecretArn();
    ISecret databaseSecret = Secret.fromSecretCompleteArn(scope, "databaseSecret", databaseSecretArn);


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
    vars.put("COGNITO_LOGOUT_URL", cognitoOutputParameters.getLogoutUrl());
    vars.put("TODO_SHARING_QUEUE_NAME", messagingOutputParameters.getTodoSharingQueueName());
    vars.put("TODO_UPDATES_TOPIC_NAME", messagingOutputParameters.getTodoUpdatesTopicName());
    vars.put("WEB_SOCKET_RELAY_HOST", activeMqOutputParameters.getStompEndpoint());

    return vars;
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }

}
