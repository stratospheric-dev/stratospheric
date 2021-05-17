package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.cloudwatch.Dashboard;
import software.amazon.awscdk.services.cloudwatch.DashboardProps;
import software.amazon.awscdk.services.cloudwatch.GraphWidget;
import software.amazon.awscdk.services.cloudwatch.GraphWidgetView;
import software.amazon.awscdk.services.cloudwatch.LogQueryVisualizationType;
import software.amazon.awscdk.services.cloudwatch.LogQueryWidget;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.SingleValueWidget;
import software.amazon.awscdk.services.cloudwatch.TextWidget;

import java.util.List;
import java.util.Map;

public class MonitoringStack extends Stack {

  public MonitoringStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment) {

    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Monitoring"))
      .env(awsEnvironment).build());

    CognitoStack.CognitoOutputParameters cognitoOutputParameters =
      CognitoStack.getOutputParametersFromParameterStore(this, applicationEnvironment);

    new Dashboard(this, "applicationDashboard", DashboardProps.builder()
      .dashboardName(applicationEnvironment + "-application-dashboard")
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
            .title("User Registrations")
            .setPeriodToTimeRange(true)
            .metrics(List.of(new Metric(MetricProps.builder()
              .namespace("stratospheric")
              .metricName("stratospheric.registration.signups.count")
              .region(awsEnvironment.getRegion())
              .statistic("sum")
              .dimensions(Map.of(
                "outcome", "success",
                "environment", applicationEnvironment.getEnvironmentName())
              )
              .build())))
            .height(6)
            .width(6)
            .build(),
          GraphWidget.Builder.create()
            .title("User Sign In")
            .view(GraphWidgetView.BAR)
            .left(List.of(new Metric(MetricProps.builder()
              .namespace("AWS/Cognito")
              .metricName("SignInSuccesses")
              .period(Duration.minutes(15))
              .region(awsEnvironment.getRegion())
              .dimensions(Map.of(
                "UserPoolClient", cognitoOutputParameters.getUserPoolClientId(),
                "UserPool", cognitoOutputParameters.getUserPoolId()))
              .statistic("sum")
              .build())))
            .right(List.of(new Metric(MetricProps.builder()
              .namespace("AWS/Cognito")
              .metricName("TokenRefreshSuccesses")
              .period(Duration.minutes(15))
              .region(awsEnvironment.getRegion())
              .dimensions(Map.of(
                "UserPoolClient", cognitoOutputParameters.getUserPoolClientId(),
                "UserPool", cognitoOutputParameters.getUserPoolId()))
              .statistic("sum")
              .build())))
            .height(6)
            .width(6)
            .build(),
          LogQueryWidget.Builder
            .create()
            .title("Backend Logs")
            .logGroupNames(List.of("staging-todo-app-logs"))
            .queryString(
              "fields @timestamp, @message" +
                "| sort @timestamp desc" +
                "| limit 20")
            .height(6)
            .width(6)
            .build()
        )
      ))
      .build());
  }
}
