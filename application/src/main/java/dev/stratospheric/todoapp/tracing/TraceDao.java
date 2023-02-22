package dev.stratospheric.todoapp.tracing;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TraceDao {

  private static final Logger LOG = LoggerFactory.getLogger(TraceDao.class);

  private final DynamoDbTemplate dynamoDbTemplate;

  public TraceDao(DynamoDbTemplate dynamoDbTemplate) {
    this.dynamoDbTemplate = dynamoDbTemplate;
  }

  @Async
  @EventListener(TracingEvent.class)
  public void storeTracingEvent(TracingEvent tracingEvent) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setId(UUID.randomUUID().toString());
    breadcrumb.setUri(tracingEvent.getUri());
    breadcrumb.setUsername(tracingEvent.getUsername());
    breadcrumb.setTimestamp(ZonedDateTime.now().toString());

    dynamoDbTemplate.save(breadcrumb);

    LOG.info("Successfully stored breadcrumb trace");
  }

  public List<Breadcrumb> findAllEventsForUser(String username) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUsername(username);

    return dynamoDbTemplate.query(
        QueryEnhancedRequest
          .builder()
          .queryConditional(
            QueryConditional.keyEqualTo(
              Key
                .builder()
                .partitionValue(breadcrumb.getId())
                .build()
            )
          )
          .build(),
        Breadcrumb.class
      ).items()
      .stream()
      .toList();
  }

  public List<Breadcrumb> findUserTraceForLastTwoWeeks(String username) {
    ZonedDateTime twoWeeksAgo = ZonedDateTime.now().minusWeeks(2);

    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUsername(username);

    return dynamoDbTemplate.query(
        QueryEnhancedRequest
          .builder()
          .queryConditional(
            QueryConditional.keyEqualTo(
              Key
                .builder()
                .partitionValue(breadcrumb.getId())
                .build()
            )
          )
          .filterExpression(
            Expression
              .builder()
              .expression("timestamp > :twoWeeksAgo")
              .putExpressionValue(":twoWeeksAgo",
                AttributeValue
                  .builder()
                  .s(twoWeeksAgo.toString())
                  .build()
              )
              .build()
          )
          .build(),
        Breadcrumb.class
      )
      .items()
      .stream()
      .toList();
  }
}
