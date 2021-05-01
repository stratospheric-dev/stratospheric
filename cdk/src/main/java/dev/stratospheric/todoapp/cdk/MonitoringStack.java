package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.sqs.IQueue;

public class MonitoringStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;

  public MonitoringStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment) {

    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Monitoring"))
      .env(awsEnvironment).build());

    this.applicationEnvironment = applicationEnvironment;
  }
}
