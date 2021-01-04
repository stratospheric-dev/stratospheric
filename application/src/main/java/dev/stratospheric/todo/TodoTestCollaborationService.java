package dev.stratospheric.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class TodoTestCollaborationService {

  private static final Logger LOG = LoggerFactory.getLogger(TodoTestCollaborationService.class.getName());

  @SendTo("/topic/todoUpdates")
  public String testConfirmCollaboration() {
    String subject = "info@stratospheric.dev";
    String message = "User nobody has accepted your collaboration request for todo #1.";

    LOG.info("Message sent to ActiveMQ broker: {} {}", subject, message);

    return subject + " " + message;
  }
}
