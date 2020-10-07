package dev.aws101.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class TodoTestCollaborationServiceImpl implements TodoTestCollaborationService {

  private final NotificationMessagingTemplate notificationMessagingTemplate;
  private final String todoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoTestCollaborationServiceImpl.class.getName());

  public TodoTestCollaborationServiceImpl(
    NotificationMessagingTemplate notificationMessagingTemplate,
    @Value("${custom.updates-topic}") String todoUpdatesTopic) {
    this.notificationMessagingTemplate = notificationMessagingTemplate;
    this.todoUpdatesTopic = todoUpdatesTopic;
  }

  @Override
  public String testConfirmCollaboration() {
    String subject = "admin@stratospheric.dev";
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
