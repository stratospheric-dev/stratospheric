package dev.stratospheric.todoapp.tracing;

import java.time.ZonedDateTime;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TraceDao {

  private static final Logger LOG = LoggerFactory.getLogger(TraceDao.class);

  private final DynamoDBMapper dynamoDBMapper;

  public TraceDao(DynamoDBMapper dynamoDBMapper) {
    this.dynamoDBMapper = dynamoDBMapper;
  }

  @Async
  @EventListener(TracingEvent.class)
  public void storeTracingEvent(TracingEvent tracingEvent) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUri(tracingEvent.getUri());
    breadcrumb.setUsername(tracingEvent.getUsername());
    breadcrumb.setTimestamp(ZonedDateTime.now().toString());

    dynamoDBMapper.save(breadcrumb);

    LOG.info("Successfully stored breadcrumb trace");
  }

  public List<Breadcrumb> findAllEventsForUser(String username) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUsername(username);

    DynamoDBQueryExpression<Breadcrumb> queryExpression =
      new DynamoDBQueryExpression<Breadcrumb>()
        .withHashKeyValues(breadcrumb);

    return dynamoDBMapper.query(Breadcrumb.class, queryExpression);
  }

  public List<Breadcrumb> findUserTraceForLastTwoWeeks(String username) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime twoWeeksAgo = now.minusWeeks(2);
    Condition timestampCondition = new Condition()
      .withComparisonOperator(ComparisonOperator.GT.toString())
      .withAttributeValueList(new AttributeValue().withS(twoWeeksAgo.toString()));

    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUsername(username);

    DynamoDBQueryExpression<Breadcrumb> queryExpression =
      new DynamoDBQueryExpression<Breadcrumb>()
        .withHashKeyValues(breadcrumb)
        .withRangeKeyCondition("timestamp", timestampCondition);

    return dynamoDBMapper.query(Breadcrumb.class, queryExpression);
  }
}
