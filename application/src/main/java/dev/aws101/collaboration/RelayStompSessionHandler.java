package dev.aws101.collaboration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class RelayStompSessionHandler extends StompSessionHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(RelayStompSessionHandler.class.getName());

  @Override
  public void afterConnected(StompSession session, @Nullable StompHeaders connectedHeaders) {
    LOG.info("New session established: {}", session.getSessionId());

    session.subscribe("/topic/todoUpdates", this);

    LOG.info("Subscribed to /topic/todoUpdates");
  }

  @Override
  public void handleException(
    @Nullable StompSession session,
    StompCommand command,
    @Nullable StompHeaders headers,
    @Nullable byte[] payload,
    @Nullable Throwable exception
  ) {
    LOG.error("An exception occured.", exception);
  }

  @Override
  public Type getPayloadType(@Nullable StompHeaders headers) {
    return String.class;
  }

  @Override
  public void handleFrame(@Nullable StompHeaders headers, Object payload) {
    String message = (String) payload;

    LOG.info("Message received: {}", message);
  }
}
