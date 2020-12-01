package dev.stratospheric.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class TodoTestCollaborationService {

  private final NotificationMessagingTemplate notificationMessagingTemplate;
  private final String todoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoTestCollaborationService.class.getName());

  public TodoTestCollaborationService(
    NotificationMessagingTemplate notificationMessagingTemplate,
    @Value("${custom.updates-topic}") String todoUpdatesTopic) {
    this.notificationMessagingTemplate = notificationMessagingTemplate;
    this.todoUpdatesTopic = todoUpdatesTopic;
  }

  public String testConfirmCollaboration() {
    String subject = "info@stratospheric.dev";
    String message = "User nobody has accepted your collaboration request for todo #1.";

    LOG.info("Message sent to AWS SNS: {} {}", subject, message);

    notificationMessagingTemplate.sendNotification(
      todoUpdatesTopic,
      message,
      subject
    );

    return subject;
  }
}
