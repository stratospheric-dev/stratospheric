package dev.stratospheric.todoapp.tracing;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class TraceDao {

  private final DynamoDBMapper dynamoDBMapper;

  public TraceDao(DynamoDBMapper dynamoDBMapper) {
    this.dynamoDBMapper = dynamoDBMapper;
  }

  @EventListener(TracingEvent.class)
  public Breadcrumb create(TracingEvent tracingEvent) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setUri(tracingEvent.getUri());
    breadcrumb.setUsername(tracingEvent.getUsername());
    breadcrumb.setTimestamp(ZonedDateTime.now().toString());

    dynamoDBMapper.save(breadcrumb);

    return breadcrumb;
  }
}
