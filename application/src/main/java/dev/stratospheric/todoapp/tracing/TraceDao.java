package dev.stratospheric.todoapp.tracing;

import java.time.ZonedDateTime;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
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
}
