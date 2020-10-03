package dev.aws101.collaboration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("${custom.updates-topic}")
public class PubSubController {

  private static final Logger LOG = LoggerFactory.getLogger(PubSubController.class.getName());

  private static final String UPDATE_TODO_URL = "/websocketEndpoints/updateTodo";

  @Value("${custom.websocket-url}")
  private String webSocketURL;

  @NotificationSubscriptionMapping
  public void confirmSubscriptionMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }

  @NotificationUnsubscribeConfirmationMapping
  public void confirmUnsubscribeMessage(NotificationStatus notificationStatus) {
    notificationStatus.confirmSubscription();
  }

  @NotificationMessageMapping
  public void receiveNotification(@NotificationSubject String subject, @NotificationMessage String message) {
    LOG.info("Todo update received. Subject {}: {}", subject, message);

    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    WebSocketClient transport = new SockJsClient(transports);
    WebSocketStompClient webSocketStompClient = new WebSocketStompClient(transport);
    webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

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
