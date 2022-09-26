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
import software.amazon.awscdk.services.cloudwatch.MathExpression;
import software.amazon.awscdk.services.cloudwatch.MathExpressionProps;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.TextWidget;
import software.constructs.Construct;

public class OperationalCloudWatchDashboard extends Construct {

  private static final String METRIC_NAMESPACE = "stratospheric";

  public OperationalCloudWatchDashboard(
    @NotNull Construct scope,
    @NotNull String id,
    ApplicationEnvironment applicationEnvironment,
    Environment awsEnvironment,
    InputParameter inputParameter) {

    super(scope, id);

    new Dashboard(this, "operationalApplicationDashboard", DashboardProps.builder()
      .dashboardName(applicationEnvironment.prefix("operational-application-dashboard"))
      .widgets(List.of(
        List.of(
          TextWidget.Builder
            .create()
            .markdown("""
              # Operations Dashboard
              Created with the AWS CDK.
              * IaC
              * Configurable
              * Nice-looking
              """)
            .height(6)
            .width(6)
            .build(),
          LogQueryWidget.Builder
            .create()
            .title("Application Logs")
            .logGroupNames(List.of(applicationEnvironment + "-logs"))
            .queryString(
              """
                  fields timestamp, message, logger, @logStream
                  | sort timestamp desc
                  | limit 100
                """)
            .height(6)
            .width(18)
            .build()
        ),
        List.of(
          GraphWidget.Builder.create()
            .title("Count Per Log Level")
            .view(GraphWidgetView.TIME_SERIES)
            .left(List.of(
                createLogMetric(applicationEnvironment, awsEnvironment, "error"),
                createLogMetric(applicationEnvironment, awsEnvironment, "warn"),
                createLogMetric(applicationEnvironment, awsEnvironment, "info")
              )
            ).height(6)
            .width(12)
            .build(),
          LogQueryWidget.Builder
            .create()
            .title("Application Error Logs")
            .logGroupNames(List.of(applicationEnvironment + "-logs"))
            .queryString(
              """
                  fields message, logger, @logStream
                  | filter (level = 'ERROR' OR level = 'WARN')
                  | sort timestamp desc
                """).height(6)
            .width(12)
            .build()),
        List.of(
          GraphWidget.Builder.create()
            .title("CPU Usage in %")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
                new MathExpression(MathExpressionProps.builder()
                  .label("JVM Process CPU Usage")
                  .expression("100*(processCpu)")
                  .usingMetrics(Map.of("processCpu", new Metric(MetricProps.builder()
                    .namespace(METRIC_NAMESPACE)
                    .metricName("process.cpu.usage.value")
                    .period(Duration.minutes(5))
                    .dimensionsMap(Map.of(
                      "environment", applicationEnvironment.getEnvironmentName()
                    ))
                    .statistic("avg")
                    .build())
                  ))
                  .build()),
                new MathExpression(MathExpressionProps.builder()
                  .label("System CPU Usage")
                  .expression("100*(systemCpu)")
                  .usingMetrics(Map.of("systemCpu", new Metric(MetricProps.builder()
                    .namespace(METRIC_NAMESPACE)
                    .metricName("system.cpu.usage.value")
                    .period(Duration.minutes(5))
                    .dimensionsMap(Map.of(
                      "environment", applicationEnvironment.getEnvironmentName()
                    ))
                    .statistic("avg")
                    .build())
                  ))
                  .build())
              )
            ).height(6)
            .width(12)
            .build(),
          GraphWidget.Builder.create()
            .title("JVM Memory Overview")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
                new MathExpression(MathExpressionProps.builder()
                  .label("JVM Heap Memory Used")
                  .expression("(survivorUsed + edenUsed + tenuredUsed)/1000000")
                  .usingMetrics(Map.of(
                    "survivorUsed", createJvmMemoryMetric(applicationEnvironment, "Survivor Space", "jvm.memory.used.value"),
                    "edenUsed", createJvmMemoryMetric(applicationEnvironment, "Eden Space", "jvm.memory.used.value"),
                    "tenuredUsed", createJvmMemoryMetric(applicationEnvironment, "Tenured Gen", "jvm.memory.used.value"))
                  )
                  .build()),
                new MathExpression(MathExpressionProps.builder()
                  .label("JVM Heap Memory Committed")
                  .expression("(survivorCommitted + edenCommitted + tenuredCommitted)/1000000")
                  .usingMetrics(Map.of(
                    "survivorCommitted", createJvmMemoryMetric(applicationEnvironment, "Survivor Space", "jvm.memory.committed.value"),
                    "edenCommitted", createJvmMemoryMetric(applicationEnvironment, "Eden Space", "jvm.memory.committed.value"),
                    "tenuredCommitted", createJvmMemoryMetric(applicationEnvironment, "Tenured Gen", "jvm.memory.committed.value"))
                  )
                  .build())
              )
            ).height(6)
            .width(12)
            .build()
        ),
        List.of(
          GraphWidget.Builder.create()
            .title("ELB Target Response Codes")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              createElbTargetResponseCountMetric("HTTPCode_Target_2XX_Count", inputParameter.beanstalkLoadBalancerId),
              createElbTargetResponseCountMetric("HTTPCode_Target_3XX_Count", inputParameter.beanstalkLoadBalancerId),
              createElbTargetResponseCountMetric("HTTPCode_Target_4XX_Count", inputParameter.beanstalkLoadBalancerId),
              createElbTargetResponseCountMetric("HTTPCode_Target_5XX_Count", inputParameter.beanstalkLoadBalancerId)
            ))
            .height(6)
            .width(12)
            .build(),
          GraphWidget.Builder.create()
            .title("ELB Avg. Response Times")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              createElbTargetResponseTimeMetric("avg", inputParameter.beanstalkLoadBalancerId),
              createElbTargetResponseTimeMetric("max", inputParameter.beanstalkLoadBalancerId),
              createElbTargetResponseTimeMetric("min", inputParameter.beanstalkLoadBalancerId)
            ))
            .height(6)
            .width(12)
            .build()
        ),
        List.of(
          GraphWidget.Builder.create()
            .title("RDS Open Connections")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              new Metric(MetricProps.builder()
                .namespace("AWS/RDS")
                .metricName("DatabaseConnections")
                .dimensionsMap(Map.of(
                  "DBInstanceIdentifier", inputParameter.rdsDatabaseIdentifier
                ))
                .period(Duration.minutes(1))
                .statistic("sum")
                .build())
            ))
            .height(6)
            .width(12)
            .build(),
          GraphWidget.Builder.create()
            .title("RDS CPU/Storage")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              new Metric(MetricProps.builder()
                .namespace("AWS/RDS")
                .metricName("CPUUtilization")
                .dimensionsMap(Map.of(
                  "DBInstanceIdentifier", inputParameter.rdsDatabaseIdentifier
                ))
                .period(Duration.minutes(1))
                .statistic("avg")
                .build())
            ))
            .right(
              List.of(
                new Metric(MetricProps.builder()
                  .namespace("AWS/RDS")
                  .metricName("FreeStorageSpace")
                  .dimensionsMap(Map.of(
                    "DBInstanceIdentifier", inputParameter.rdsDatabaseIdentifier
                  ))
                  .period(Duration.minutes(1))
                  .statistic("sum")
                  .build())
              )
            )
            .height(6)
            .width(12)
            .build()
        ),
        List.of(
          GraphWidget.Builder.create()
            .title("SQS Queue Metrics")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              createSqsMetric("ApproximateAgeOfOldestMessage", applicationEnvironment.prefix("todo-sharing-queue"), "avg"),
              createSqsMetric("ApproximateNumberOfMessagesVisible", applicationEnvironment.prefix("todo-sharing-queue"), "sum"),
              createSqsMetric("NumberOfMessagesSent", applicationEnvironment.prefix("todo-sharing-queue"), "sum")
              ))
            .height(6)
            .width(12)
            .build(),
          GraphWidget.Builder.create()
            .title("SQS DLQ Metrics")
            .view(GraphWidgetView.TIME_SERIES)
            .region(awsEnvironment.getRegion())
            .setPeriodToTimeRange(true)
            .left(List.of(
              createSqsMetric("ApproximateAgeOfOldestMessage", applicationEnvironment.prefix("todo-sharing-dead-letter-queue"), "avg"),
              createSqsMetric("ApproximateNumberOfMessagesVisible", applicationEnvironment.prefix("todo-sharing-dead-letter-queue"), "sum"),
              createSqsMetric("NumberOfMessagesSent", applicationEnvironment.prefix("todo-sharing-dead-letter-queue"), "sum")
            ))
            .height(6)
            .width(12)
            .build()
        ))
      ).build());
  }

  @NotNull
  private Metric createSqsMetric(String metricName, String queueName, String statistic) {
    return new Metric(MetricProps.builder()
      .namespace("AWS/SQS")
      .metricName(metricName)
      .dimensionsMap(Map.of(
        "QueueName", queueName
      ))
      .period(Duration.minutes(1))
      .statistic(statistic)
      .build());
  }

  @NotNull
  private Metric createElbTargetResponseTimeMetric(String statistic, String loadBalancerId) {
    return new Metric(MetricProps.builder()
      .namespace("AWS/ApplicationELB")
      .metricName("TargetResponseTime")
      .dimensionsMap(Map.of(
        "LoadBalancer", loadBalancerId
      ))
      .period(Duration.minutes(1))
      .statistic(statistic)
      .build());
  }

  @NotNull
  private Metric createElbTargetResponseCountMetric(String metricName, String loadBalancerId) {
    return new Metric(MetricProps.builder()
      .namespace("AWS/ApplicationELB")
      .metricName(metricName)
      .dimensionsMap(Map.of(
        "LoadBalancer", loadBalancerId
      ))
      .period(Duration.minutes(15))
      .statistic("sum")
      .build());
  }

  @NotNull
  private Metric createJvmMemoryMetric(ApplicationEnvironment applicationEnvironment, String id, String metricName) {
    return new Metric(MetricProps.builder()
      .namespace(METRIC_NAMESPACE)
      .metricName(metricName)
      .dimensionsMap(Map.of(
        "environment", applicationEnvironment.getEnvironmentName(),
        "area", "heap",
        "id", id
      ))
      .period(Duration.minutes(1))
      .statistic("avg")
      .build());
  }

  @NotNull
  private Metric createLogMetric(ApplicationEnvironment applicationEnvironment, Environment environment, String logLevel) {
    return new Metric(MetricProps.builder()
      .namespace(METRIC_NAMESPACE)
      .region(environment.getRegion())
      .metricName("logback.events.count")
      .period(Duration.minutes(5))
      .dimensionsMap(Map.of(
        "environment", applicationEnvironment.getEnvironmentName(),
        "level", logLevel
      ))
      .statistic("sum")
      .build());
  }

  public record InputParameter(String rdsDatabaseIdentifier, String beanstalkLoadBalancerId) {
  }
}
