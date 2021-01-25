package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.IQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ssm.StringParameter;

class MessagingStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;
  private final IQueue todoSharingQueue;
  private final IQueue todoSharingDlq;
  private final ITopic todoUpdatesTopic;

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
      .queueName(applicationEnvironment.prefix("stratospheric-todo-sharing-dead-letter-queue"))
      .retentionPeriod(Duration.days(14))
      .build();

    this.todoSharingQueue = Queue.Builder.create(this, "todoSharingQueue")
      .queueName(applicationEnvironment.prefix("stratospheric-todo-sharing"))
      .visibilityTimeout(Duration.minutes(5))
      .retentionPeriod(Duration.days(14))
      .deadLetterQueue(DeadLetterQueue.builder()
        .queue(todoSharingDlq)
        .maxReceiveCount(3)
        .build())
      .build();

    this.todoUpdatesTopic = Topic.Builder.create(this, "todoUpdates")
      .topicName("stratospheric-todo-updates")
      .build();

    createOutputParameters();

    applicationEnvironment.tag(this);
  }

  private static final String PARAMETER_TODO_SHARING_QUEUE_NAME = "todoSharingQueueName";
  private static final String PARAMETER_TODO_UPDATES_TOPIC_NAME = "todoUpdatesTopicName";

  private void createOutputParameters() {

    StringParameter todoSharingQueueName = StringParameter.Builder.create(this, "todoSharingQueueName")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_TODO_SHARING_QUEUE_NAME))
      .stringValue(this.todoSharingQueue.getQueueName())
      .build();

    StringParameter todoUpdatesTopicName = StringParameter.Builder.create(this, "todoUpdatesTopicName")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_TODO_UPDATES_TOPIC_NAME))
      .stringValue(this.todoUpdatesTopic.getTopicName())
      .build();

  }

  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-Messaging-" + parameterName;
  }

  public static String getTodoSharingQueueName(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_TODO_SHARING_QUEUE_NAME, createParameterName(applicationEnvironment, PARAMETER_TODO_SHARING_QUEUE_NAME))
      .getStringValue();
  }

  public static String getTodoUpdatesTopicName(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_TODO_UPDATES_TOPIC_NAME, createParameterName(applicationEnvironment, PARAMETER_TODO_UPDATES_TOPIC_NAME))
      .getStringValue();
  }

  public static MessagingOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new MessagingOutputParameters(
      getTodoSharingQueueName(scope, applicationEnvironment),
      getTodoUpdatesTopicName(scope, applicationEnvironment)
    );
  }

  public static class MessagingOutputParameters {
    private final String todoSharingQueueName;
    private final String todoUpdatesTopicName;

    public MessagingOutputParameters(String todoSharingQueueName, String todoUpdatesTopicName) {
      this.todoSharingQueueName = todoSharingQueueName;
      this.todoUpdatesTopicName = todoUpdatesTopicName;
    }

    public String getTodoSharingQueueName() {
      return todoSharingQueueName;
    }

    public String getTodoUpdatesTopicName() {
      return todoUpdatesTopicName;
    }
  }

}
