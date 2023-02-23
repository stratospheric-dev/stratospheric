package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.cloudwatch.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.synthetics.CfnCanary;
import software.amazon.awscdk.services.synthetics.CfnCanary.CodeProperty;
import software.amazon.awscdk.services.synthetics.CfnCanary.RunConfigProperty;
import software.amazon.awscdk.services.synthetics.CfnCanary.ScheduleProperty;
import software.constructs.Construct;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static java.util.Collections.singletonList;

public class CanaryStack extends Stack {

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

    String canaryName = applicationEnvironment.prefix("canary", 21);

    // There are no stable L2 constructs available yet. Hence, we fall back to the L1 CloudFormation construct
    // However, there is an experimental construct library available @aws-cdk/aws-synthetics-alpha which
    // may become GA in the near future.

    CfnCanary.Builder.create(this, "canary")
      .name(canaryName)
      .runtimeVersion("syn-nodejs-puppeteer-3.9")
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

    new Alarm(this, "canaryAlarm", AlarmProps.builder()
      .alarmName("canary-failed-alarm")
      .alarmDescription("Alert on multiple Canary failures")
      .metric(new Metric(MetricProps.builder()
        .namespace("CloudWatchSynthetics")
        .metricName("Failed")
        .dimensionsMap(
          Map.of("CanaryName", canaryName)
        )
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
    try (Scanner scanner = new Scanner(Path.of(path))) {
      StringBuilder script = new StringBuilder();
      while (scanner.hasNextLine()) {
        script.append(scanner.nextLine());
        script.append("\n");
      }
      return script.toString();
    }
  }
}
