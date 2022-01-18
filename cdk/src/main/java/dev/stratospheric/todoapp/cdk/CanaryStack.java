package dev.stratospheric.todoapp.cdk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cloudwatch.Alarm;
import software.amazon.awscdk.services.cloudwatch.AlarmProps;
import software.amazon.awscdk.services.cloudwatch.ComparisonOperator;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.TreatMissingData;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.synthetics.CfnCanary;
import software.amazon.awscdk.services.synthetics.CfnCanary.CodeProperty;
import software.amazon.awscdk.services.synthetics.CfnCanary.RunConfigProperty;
import software.amazon.awscdk.services.synthetics.CfnCanary.ScheduleProperty;
import software.constructs.Construct;

import static java.util.Collections.singletonList;

public class CanaryStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;

  public CanaryStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String applicationUrl,
    final String username,
    final String password
  ) throws IOException {
    super(
      scope,
      id,
      StackProps.builder()
        .stackName(applicationEnvironment.prefix("Canary"))
        .env(awsEnvironment)
        .build()
    );

    this.applicationEnvironment = applicationEnvironment;

    Bucket bucket = Bucket.Builder.create(this, "canaryBucket")
      .bucketName(applicationEnvironment.prefix("canary-bucket"))
      .removalPolicy(RemovalPolicy.DESTROY)
      .autoDeleteObjects(true)
      .build();

    Role executionRole = Role.Builder.create(this, "canaryExecutionRole")
      .roleName(applicationEnvironment.prefix("canary-execution-role"))
      .assumedBy(new AnyPrincipal())
      .inlinePolicies(Map.of(
        applicationEnvironment.prefix("canaryExecutionRolePolicy"),
        PolicyDocument.Builder.create()
          .statements(singletonList(PolicyStatement.Builder.create()
            .effect(Effect.ALLOW)
            .resources(singletonList("*"))
            .actions(Arrays.asList(
              "s3:PutObject",
              "s3:GetBucketLocation",
              "s3:ListAllMyBuckets",
              "cloudwatch:PutMetricData",
              "logs:CreateLogGroup",
              "logs:CreateLogStream",
              "logs:PutLogEvents"))
            .build()))
          .build()))
      .build();

    // It's not yet possible to create environment variables with the Level 2 Canary construct, so we have
    // to fall back to the Level 1 CloudFormation construct.
    // See https://github.com/aws/aws-cdk/issues/10515.

    CfnCanary.Builder.create(this, "canary")
      .name(applicationEnvironment.prefix("canary", 21))
      .runtimeVersion("syn-nodejs-puppeteer-3.1")
      .artifactS3Location(bucket.s3UrlForObject("create-todo-canary"))
      .startCanaryAfterCreation(Boolean.TRUE)
      .executionRoleArn(executionRole.getRoleArn())
      .schedule(ScheduleProperty.builder()
        .expression("rate(15 minutes)")
        .build())
      .runConfig(RunConfigProperty.builder()
        .environmentVariables(Map.of(
          "TARGET_URL", applicationUrl,
          "USER_NAME", username,
          "PASSWORD", password
        ))
        .timeoutInSeconds(30)
        .build())
      .code(CodeProperty.builder()
        .handler("recordedScript.handler")
        .script(getScriptFromResource("canaries/create-todo-canary.js"))
        .build())
      .build();

    Alarm canaryAlarm = new Alarm(this, "canaryAlarm", AlarmProps.builder()
      .alarmName("canary-failed-alarm")
      .alarmDescription("Alert on multiple Canary failures")
      .metric(new Metric(MetricProps.builder()
        .namespace("CloudWatchSynthetics")
        .metricName("Failed")
        .region(awsEnvironment.getRegion())
        .period(Duration.minutes(50))
        .statistic("sum")
        .build()))
      .treatMissingData(TreatMissingData.NOT_BREACHING)
      .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
      .evaluationPeriods(1)
      .threshold(3)
      .actionsEnabled(false)
      .build());

  }

  private String getScriptFromResource(String path) throws IOException {
    Scanner scanner = new Scanner(Path.of(path));
    StringBuilder script = new StringBuilder();
    while (scanner.hasNextLine()) {
      script.append(scanner.nextLine());
      script.append("\n");
    }
    return script.toString();
  }
}
