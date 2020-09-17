package dev.aws101.collaboration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class TodoSharingListener {

  private final MailSender mailSender;

  private static final Logger LOG = LoggerFactory.getLogger(TodoSharingListener.class.getName());

  public TodoSharingListener(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  @SqsListener(value = "${custom.sharing-queue}")
  public void listenToSharingMessages(TodoCollaborationRequest payload) {
    LOG.info("Incoming todo sharing payload: " + payload);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("noreply@stratospheric.dev");
    message.setTo(payload.getCollaboratorEmail());
    message.setSubject("A todo was shared with you");
    message.setText(String.format("Hi %s, \n \n" +
        "someone shared a Todo from https://app.stratospheric.dev with you.\n \n" +
        "Information about the shared Todo item: \n \n" +
        "Title: %s \n" +
        "Description: %s \n" +
        "Priority: %s \n" +
        "\n" +
        // (Optional) TODO: Implement feature to accept confirmation
        "You can accept the collaboration by clicking this link: https://app.stratospheric.dev/confirmCollaboration?token=123 \n \n" +
        "Kind regards, \n" +
        "AWS101",
      payload.getCollaboratorName(), payload.getTodoTitle(), payload.getTodoDescription(), payload.getTodoPriority()));
    mailSender.send(message);

    LOG.info("Successfully informed collaborator about shared todo");
  }
}
