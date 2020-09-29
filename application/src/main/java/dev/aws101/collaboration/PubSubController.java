package dev.aws101.collaboration;

import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${custom.updates-topic}")
public class PubSubController {

  @NotificationSubscriptionMapping
  public void confirmUnsubscribeMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }

  @NotificationMessageMapping
  @SendTo("/topic/todoUpdates")
  public void receiveNotification(@NotificationMessage String message, @NotificationSubject String subject) {
    // TODO: Implement method.
  }

  @NotificationUnsubscribeConfirmationMapping
  public void confirmSubscriptionMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }
}
