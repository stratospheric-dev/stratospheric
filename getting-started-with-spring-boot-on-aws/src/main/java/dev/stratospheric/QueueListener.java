package dev.stratospheric;

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueueListener.class);

  @SqsListener(value = "${custom.sqs-queue-name}")
  public void onS3UploadEvent(S3EventNotification event) {
    LOGGER.info("Incoming S3EventNotification: {}", event);

    if (event.getRecords() == null) {
      return;
    }

    if (event.getRecords().size() > 0) {
      String bucket = event.getRecords().get(0).getS3().getBucket().getName();
      String key = event.getRecords().get(0).getS3().getObject().getKey();

      Message<String> payload = MessageBuilder
        .withPayload("New upload happened: " + bucket + "/" + key)
        .build();
    }
  }
}
