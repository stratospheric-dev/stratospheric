package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.PostgresDatabase;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import static dev.stratospheric.todoapp.cdk.Validations.requireNonEmpty;

public class Chapter11App {
  public static void main(final String[] args) {
    App app = new App();

    String environmentName = "chapter-11";
    String applicationName = "todo-app-chapter-11";
    String sslCertificateArn = "arn:aws:acm:eu-central-1:221875718260:certificate/8d92169c-ea74-4086-b407-b951429ac2b1";

    String accountId = (String) app.getNode().tryGetContext("accountId");
    requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    requireNonEmpty(region, "context variable 'region' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    Stack networkStack = new Stack(app, "NetworkStack", StackProps.builder()
      .stackName(environmentName + "-Network")
      .env(awsEnvironment)
      .build());

    new Network(
      networkStack,
      "Network",
      awsEnvironment,
      environmentName,
      new Network.NetworkInputParameters(sslCertificateArn));

    Stack databaseStack = new Stack(app, "DatabaseStack", StackProps.builder()
      .stackName(applicationEnvironment.prefix("Database"))
      .env(awsEnvironment)
      .build());

    new PostgresDatabase(
      databaseStack,
      "Database",
      awsEnvironment,
      applicationEnvironment,
      new PostgresDatabase.DatabaseInputParameters());

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }
}
