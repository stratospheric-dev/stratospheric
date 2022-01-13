package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.IQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

class MessagingStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;
  private final IQueue todoSharingQueue;
  private final IQueue todoSharingDlq;

  public MessagingStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Messaging"))
      .env(awsEnvironment).build());

    this.applicationEnvironment = applicationEnvironment;

    this.todoSharingDlq = Queue.Builder.create(this, "todoSharingDlq")
      .queueName(applicationEnvironment.prefix( "todo-sharing-dead-letter-queue"))
      .retentionPeriod(Duration.days(14))
      .build();

    this.todoSharingQueue = Queue.Builder.create(this, "todoSharingQueue")
      .queueName(applicationEnvironment.prefix("todo-sharing-queue"))
      .visibilityTimeout(Duration.seconds(30))
      .retentionPeriod(Duration.days(14))
      .deadLetterQueue(DeadLetterQueue.builder()
        .queue(todoSharingDlq)
        .maxReceiveCount(3)
        .build())
      .build();

    createOutputParameters();

    applicationEnvironment.tag(this);
  }

  private static final String PARAMETER_TODO_SHARING_QUEUE_NAME = "todoSharingQueueName";

  private void createOutputParameters() {

    StringParameter.Builder.create(this, "todoSharingQueueName")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_TODO_SHARING_QUEUE_NAME))
      .stringValue(this.todoSharingQueue.getQueueName())
      .build();

  }

  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-Messaging-" + parameterName;
  }

  public static String getTodoSharingQueueName(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_TODO_SHARING_QUEUE_NAME, createParameterName(applicationEnvironment, PARAMETER_TODO_SHARING_QUEUE_NAME))
      .getStringValue();
  }

  public static MessagingOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new MessagingOutputParameters(
      getTodoSharingQueueName(scope, applicationEnvironment)
    );
  }

  public static class MessagingOutputParameters {
    private final String todoSharingQueueName;

    public MessagingOutputParameters(String todoSharingQueueName) {
      this.todoSharingQueueName = todoSharingQueueName;
    }

    public String getTodoSharingQueueName() {
      return todoSharingQueueName;
    }
  }

}
