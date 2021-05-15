package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.cloudwatch.Dashboard;
import software.amazon.awscdk.services.cloudwatch.DashboardProps;
import software.amazon.awscdk.services.cloudwatch.GraphWidget;
import software.amazon.awscdk.services.cloudwatch.LogQueryWidget;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.SingleValueWidget;
import software.amazon.awscdk.services.cloudwatch.TextWidget;

import java.util.List;
import java.util.Map;

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

    CognitoStack.CognitoOutputParameters cognitoOutputParameters =
      CognitoStack.getOutputParametersFromParameterStore(this, applicationEnvironment);

    new Dashboard(this, "applicationDashboard", DashboardProps.builder()
      .dashboardName(applicationEnvironment.getApplicationName() + "-application-dashboard")
      .widgets(List.of(
        List.of(
          TextWidget.Builder
            .create()
            .markdown("# Stratospheric Dashboard \n Created with AWS CDK. \n * IaC \n * Configurable \n * Nice-looking")
            .height(6)
            .width(6)
            .build(),
          SingleValueWidget.Builder
            .create()
            .width(6)
            .height(6)
            .title("User Registrations")
            .metrics(List.of(new Metric(MetricProps.builder()
              .namespace("stratospheric")
              .metricName("stratospheric.registration.users.count")
              .statistic("sum")
              .dimensions(Map.of("outcome", "success"))
              .build())))
            .build(),
          GraphWidget.Builder.create()
            .title("Number of registrations")
            .height(6)
            .width(6)
            .left(List.of(new Metric(MetricProps.builder()
              .namespace("Cognito")
              .metricName("SignInSuccess")
              .dimensions(Map.of(
                "UserPoolClient", cognitoOutputParameters.getUserPoolClientId(),
                "UserPool", cognitoOutputParameters.getUserPoolId()))
              .statistic("sum")
              .build())))
            .build(),
          LogQueryWidget.Builder
            .create()
            .height(6)
            .width(6)
            .title("Logs")
            .logGroupNames(List.of("staging-todo-app-logs"))
            .queryString(
              "fields @timestamp, @message" +
                "| sort @timestamp desc" +
                "| limit 20")
            .build()
        )
      ))
      .build());
  }
}
