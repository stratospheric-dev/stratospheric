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
import software.amazon.awscdk.services.cloudwatch.TextWidget;

import java.util.List;

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

    new Dashboard(this, "applicationDashboard", DashboardProps.builder()
      .dashboardName(applicationEnvironment.getApplicationName())
      .widgets(List.of(
        List.of(
          TextWidget.Builder.create().markdown("# Monitoring Dashboard - proudly created with CDK").build(),
          GraphWidget.Builder.create().title("Number of registrations").build(),
          LogQueryWidget.Builder.create().title("Logs").logGroupNames(List.of("staging-todo-app-logs")).queryString(
            "fields @timestamp, @message" +
              "| sort @timestamp desc" +
              "| limit 20")
            .build()
        )
      ))
      .build());
  }
}
