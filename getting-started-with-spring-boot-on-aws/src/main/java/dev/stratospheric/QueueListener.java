package dev.stratospheric;

import com.amazonaws.services.s3.event.S3EventNotification;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueueListener.class);

  private final QueueMessagingTemplate queueMessagingTemplate;
  private final NotificationMessagingTemplate notificationMessagingTemplate;

  public QueueListener(QueueMessagingTemplate queueMessagingTemplate,
                       NotificationMessagingTemplate notificationMessagingTemplate) {
    this.queueMessagingTemplate = queueMessagingTemplate;
    this.notificationMessagingTemplate = notificationMessagingTemplate;
  }

  @SqsListener(value = "${custom.sqs-queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void onS3UploadEvent(S3EventNotification event) {
    LOGGER.info("Incoming S3EventNotification: " + event.toJson());

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

    // this.queueMessagingTemplate.convertAndSend("queueNameToNotify", payload);
    // this.notificationMessagingTemplate.convertAndSend("topicNameToNotify", payload);
  }
}
