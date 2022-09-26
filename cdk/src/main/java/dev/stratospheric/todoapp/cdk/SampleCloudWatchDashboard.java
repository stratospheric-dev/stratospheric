package dev.stratospheric.todoapp.cdk;

import java.util.List;
import java.util.Map;

import dev.stratospheric.cdk.ApplicationEnvironment;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.services.cloudwatch.Dashboard;
import software.amazon.awscdk.services.cloudwatch.DashboardProps;
import software.amazon.awscdk.services.cloudwatch.GraphWidget;
import software.amazon.awscdk.services.cloudwatch.GraphWidgetView;
import software.amazon.awscdk.services.cloudwatch.LogQueryWidget;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.SingleValueWidget;
import software.amazon.awscdk.services.cloudwatch.TextWidget;
import software.constructs.Construct;

public class SampleCloudWatchDashboard extends Construct {

  public SampleCloudWatchDashboard(
    @NotNull Construct scope,
    @NotNull String id,
    ApplicationEnvironment applicationEnvironment,
    Environment awsEnvironment,
    InputParameter inputParameter) {

    super(scope, id);

    new Dashboard(this, "sampleApplicationDashboard", DashboardProps.builder()
      .dashboardName(applicationEnvironment + "-sample-application-dashboard")
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
              .dimensionsMap(Map.of(
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
              .dimensionsMap(Map.of(
                "UserPoolClient", inputParameter.cognitoUserPoolClientId(),
                "UserPool", inputParameter.cognitoUserPoolId()))
              .statistic("sum")
              .build())))
            .right(List.of(new Metric(MetricProps.builder()
              .namespace("AWS/Cognito")
              .metricName("TokenRefreshSuccesses")
              .period(Duration.minutes(15))
              .region(awsEnvironment.getRegion())
              .dimensionsMap(Map.of(
                "UserPoolClient", inputParameter.cognitoUserPoolClientId(),
                "UserPool", inputParameter.cognitoUserPoolId()))
              .statistic("sum")
              .build())))
            .height(6)
            .width(6)
            .build(),
          LogQueryWidget.Builder
            .create()
            .title("Backend Logs")
            .logGroupNames(List.of(applicationEnvironment + "-logs"))
            .queryString(
              "fields @timestamp, @message" +
                "| sort @timestamp desc" +
                "| limit 20" +
                "| display timestamp, message")
            .height(6)
            .width(6)
            .build()
        )
      ))
      .build());
  }

  record InputParameter(
    String cognitoUserPoolClientId,
    String cognitoUserPoolId
  ) {
  }
}
