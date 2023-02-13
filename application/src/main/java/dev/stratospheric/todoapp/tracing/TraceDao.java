package dev.stratospheric.todoapp.tracing;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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
    breadcrumb.setUri(tracingEvent.getUri());
    breadcrumb.setUsername(tracingEvent.getUsername());
    breadcrumb.setTimestamp(ZonedDateTime.now().toString());

    dynamoDbTemplate.save(breadcrumb);

    LOG.info("Successfully stored breadcrumb trace");
  }

  public List<Breadcrumb> findAllEventsForUser(String username) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUsername(username);

    // TODO: Convert to AWS SDK v2
//    DynamoDb<Breadcrumb> queryExpression =
//      new DynamoDBQueryExpression<Breadcrumb>()
//        .withHashKeyValues(breadcrumb);
//
//    return dynamoDBMapper.query(Breadcrumb.class, queryExpression);
    return List.of();
  }

  public List<Breadcrumb> findUserTraceForLastTwoWeeks(String username) {
    // TODO: Convert to AWS SDK v2

//    ZonedDateTime now = ZonedDateTime.now();
//    ZonedDateTime twoWeeksAgo = now.minusWeeks(2);
//    Condition timestampCondition = new Condition()
//      .withComparisonOperator(ComparisonOperator.GT.toString())
//      .withAttributeValueList(new AttributeValue().withS(twoWeeksAgo.toString()));
//
//    Breadcrumb breadcrumb = new Breadcrumb();
//    breadcrumb.setUsername(username);
//
//    DynamoDBQueryExpression<Breadcrumb> queryExpression =
//      new DynamoDBQueryExpression<Breadcrumb>()
//        .withHashKeyValues(breadcrumb)
//        .withRangeKeyCondition("timestamp", timestampCondition);
//
//    return dynamoDBMapper.query(Breadcrumb.class, queryExpression);
    return List.of();
  }
}
