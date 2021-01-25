package dev.stratospheric.todoapp.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class TodoTestCollaborationService {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private static final Logger LOG = LoggerFactory.getLogger(TodoTestCollaborationService.class.getName());

  public TodoTestCollaborationService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public String testConfirmCollaboration() {
    String name = "Duke";
    String subject = "Collaboration confirmed.";
    String message = "User "
      + name
      + " has accepted your collaboration request for todo #1.";

    simpMessagingTemplate.convertAndSend("/topic/todoUpdates", subject + " " + message);

    LOG.info("Message sent to ActiveMQ broker: {} {}", subject, message);

    return message;
  }
}
