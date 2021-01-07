package dev.stratospheric.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final String webSocketRelayHost;
  private final int webSocketRelayPort;

  public WebSocketConfig(
    @Value("${custom.web-socket-relay-host}") String webSocketRelayHost,
    @Value("${custom.web-socket-relay-port}") int webSocketRelayPort) {
    this.webSocketRelayHost = webSocketRelayHost;
    this.webSocketRelayPort = webSocketRelayPort;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config
      .enableStompBrokerRelay("/topic")
      .setRelayHost(webSocketRelayHost)
      .setRelayPort(webSocketRelayPort);
    config.setApplicationDestinationPrefixes("/websocketEndpoints");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/websocket")
      .withSockJS();
  }
}
