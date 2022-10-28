package dev.stratospheric.todoapp.cdk;

import java.util.Map;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.PostgresDatabase;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cloudwatch.Alarm;
import software.amazon.awscdk.services.cloudwatch.AlarmProps;
import software.amazon.awscdk.services.cloudwatch.AlarmRule;
import software.amazon.awscdk.services.cloudwatch.AlarmState;
import software.amazon.awscdk.services.cloudwatch.ComparisonOperator;
import software.amazon.awscdk.services.cloudwatch.CompositeAlarm;
import software.amazon.awscdk.services.cloudwatch.CompositeAlarmProps;
import software.amazon.awscdk.services.cloudwatch.CreateAlarmOptions;
import software.amazon.awscdk.services.cloudwatch.Metric;
import software.amazon.awscdk.services.cloudwatch.MetricOptions;
import software.amazon.awscdk.services.cloudwatch.MetricProps;
import software.amazon.awscdk.services.cloudwatch.TreatMissingData;
import software.amazon.awscdk.services.cloudwatch.actions.SnsAction;
import software.amazon.awscdk.services.logs.FilterPattern;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.MetricFilter;
import software.amazon.awscdk.services.logs.MetricFilterProps;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.TopicProps;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;
import software.constructs.Construct;

public class MonitoringStack extends Stack {

  public MonitoringStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String confirmationEmail) {

    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Monitoring"))
      .env(awsEnvironment).build());

    CognitoStack.CognitoOutputParameters cognitoOutputParameters =
      CognitoStack.getOutputParametersFromParameterStore(this, applicationEnvironment);

    Network.NetworkOutputParameters networkOutputParameters =
      Network.getOutputParametersFromParameterStore(this, applicationEnvironment.getEnvironmentName());

    PostgresDatabase.DatabaseOutputParameters databaseOutputParameters =
      PostgresDatabase.getOutputParametersFromParameterStore(this, applicationEnvironment);

    String loadBalancerName = Fn
      .split(":loadbalancer/", networkOutputParameters.getLoadBalancerArn(), 2)
      .get(1);

    new SampleCloudWatchDashboard(this, "sampleCloudWatchDashboard", applicationEnvironment,
      awsEnvironment,
      new SampleCloudWatchDashboard.InputParameter(
        cognitoOutputParameters.getUserPoolClientId(),
        cognitoOutputParameters.getUserPoolId()
      ));

    new OperationalCloudWatchDashboard(this, "operationalCloudWatchDashboard", applicationEnvironment,
      awsEnvironment,
      new OperationalCloudWatchDashboard.InputParameter(
        databaseOutputParameters.getInstanceId(),
        loadBalancerName
      ));

    Alarm elbSlowResponseTimeAlarm = new Alarm(this, "elbSlowResponseTimeAlarm", AlarmProps.builder()
      .alarmName("slow-api-response-alarm")
      .alarmDescription("Indicating potential problems with the Spring Boot Backend")
      .metric(new Metric(MetricProps.builder()
        .namespace("AWS/ApplicationELB")
        .metricName("TargetResponseTime")
        .dimensionsMap(Map.of(
          "LoadBalancer", loadBalancerName
        ))
        .region(awsEnvironment.getRegion())
        .period(Duration.minutes(5))
        .statistic("avg")
        .build()))
      .treatMissingData(TreatMissingData.NOT_BREACHING)
      .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
      .evaluationPeriods(3)
      .threshold(2)
      .actionsEnabled(true)
      .build());

    Alarm elb5xxAlarm = new Alarm(this, "elb5xxAlarm", AlarmProps.builder()
      .alarmName("5xx-backend-alarm")
      .alarmDescription("Alert on multiple HTTP 5xx ELB responses." +
        "See the runbook for a diagnosis and mitigation hints: https://github.com/stratospheric-dev/stratospheric/blob/main/docs/runbooks/elb5xxAlarm.md")
      .metric(new Metric(MetricProps.builder()
        .namespace("AWS/ApplicationELB")
        .metricName("HTTPCode_ELB_5XX_Count")
        .dimensionsMap(Map.of(
          "LoadBalancer", loadBalancerName
        ))
        .region(awsEnvironment.getRegion())
        .period(Duration.minutes(5))
        .statistic("sum")
        .build()))
      .treatMissingData(TreatMissingData.NOT_BREACHING)
      .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
      .evaluationPeriods(3)
      .datapointsToAlarm(3)
      .threshold(5)
      .actionsEnabled(false)
      .build());

    MetricFilter errorLogsMetricFilter = new MetricFilter(this, "errorLogsMetricFilter",
      MetricFilterProps.builder()
        .metricName("backend-error-logs")
        .metricNamespace("stratospheric")
        .metricValue("1")
        .defaultValue(0)
        .logGroup(LogGroup.fromLogGroupName(this, "applicationLogGroup", applicationEnvironment + "-logs"))
        .filterPattern(FilterPattern.stringValue("$.level", "=", "ERROR")) // { $.level = "ERROR" }
        .build());

    Metric errorLogsMetric = errorLogsMetricFilter.metric(MetricOptions.builder()
      .period(Duration.minutes(5))
      .statistic("sum")
      .region(awsEnvironment.getRegion())
      .build());

    Alarm errorLogsAlarm = errorLogsMetric.createAlarm(this, "errorLogsAlarm", CreateAlarmOptions.builder()
      .alarmName("backend-error-logs-alarm")
      .alarmDescription("Alert on multiple ERROR backend logs")
      .treatMissingData(TreatMissingData.NOT_BREACHING)
      .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
      .evaluationPeriods(3)
      .threshold(5)
      .actionsEnabled(false)
      .build());

    CompositeAlarm compositeAlarm = new CompositeAlarm(this, "basicCompositeAlarm",
      CompositeAlarmProps.builder()
        .actionsEnabled(true)
        .compositeAlarmName("backend-api-failure")
        .alarmDescription("Showcasing a Composite Alarm")
        .alarmRule(AlarmRule.allOf(
            AlarmRule.fromAlarm(elb5xxAlarm, AlarmState.ALARM),
            AlarmRule.fromAlarm(errorLogsAlarm, AlarmState.ALARM)
          )
        )
        .build());

    Topic snsAlarmingTopic = new Topic(this, "snsAlarmingTopic", TopicProps.builder()
      .topicName(applicationEnvironment + "-alarming-topic")
      .displayName("SNS Topic to further route Amazon CloudWatch Alarms")
      .build());

    snsAlarmingTopic.addSubscription(EmailSubscription.Builder
      .create(confirmationEmail)
      .build()
    );

    elbSlowResponseTimeAlarm.addAlarmAction(new SnsAction(snsAlarmingTopic));
    compositeAlarm.addAlarmAction(new SnsAction(snsAlarmingTopic));
  }
}
