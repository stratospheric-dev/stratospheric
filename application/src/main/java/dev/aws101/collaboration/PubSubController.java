package dev.aws101.collaboration;

import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("${custom.updates-topic}")
public class PubSubController {

  private static final Logger LOG = LoggerFactory.getLogger(PubSubController.class.getName());

  private static final String UPDATE_TODO_URL = "/websocketEndpoints/updateTodo";

  private final PersonRepository personRepository;

  private final WebSocketStompClient webSocketStompClient;

  @Value("${custom.websocket-url}")
  private String webSocketURL;

  public PubSubController(PersonRepository personRepository, WebSocketStompClient webSocketStompClient) {
    this.personRepository = personRepository;
    this.webSocketStompClient = webSocketStompClient;
  }

  @NotificationSubscriptionMapping
  public void confirmSubscriptionMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }

  @NotificationUnsubscribeConfirmationMapping
  public void confirmUnsubscribeMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }

  @NotificationMessageMapping
  public void receiveNotification(
    @NotificationSubject String subject,
    @NotificationMessage String message,
    Principal principal
  ) {
    LOG.info("Todo update received. Subject '{}': {}", subject, message);

    Person person = personRepository.findByName("Admin").orElse(null);
    if (principal != null) {
      person = personRepository.findByName(principal.getName()).orElse(null);
    }

    if (person != null && person.getEmail().equals(subject)) {
      try {
        StompSession stompSession = webSocketStompClient.connect(webSocketURL, new RelayStompSessionHandler()).get();
        stompSession.send(UPDATE_TODO_URL, message);
      } catch (InterruptedException e) {
        LOG.error(e.getMessage());
        Thread.currentThread().interrupt();
      } catch (ExecutionException ee) {
        LOG.error("ExecutionException: ", ee);
      }
    }
  }
}
