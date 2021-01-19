package dev.stratospheric.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final Endpoint websocketEndpoint;

  public WebSocketConfig(
    @Value("${custom.web-socket-relay-endpoint}") String websocketRelayEndpoint) {
    this.websocketEndpoint = Endpoint.fromEndpointString(websocketRelayEndpoint);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config
      .enableStompBrokerRelay("/topic")
      .setRelayHost(this.websocketEndpoint.host)
      .setRelayPort(this.websocketEndpoint.port);
    config.setApplicationDestinationPrefixes("/websocketEndpoints");
  }

  private static class Endpoint {
    final String host;
    final int port;

    public Endpoint(String host, int port) {
      this.host = host;
      this.port = port;
    }

    static Endpoint fromEndpointString(String endpoint) {
      Pattern pattern = Pattern.compile("^(.*):([0-9]+$)");
      Matcher matcher = pattern.matcher(endpoint);
      if (!matcher.matches()) {
        throw new IllegalStateException(String.format("invalid endpoint string (must consist of hostname and port): %s", endpoint));
      }
      String host = matcher.group(1);
      String port = matcher.group(2);
      return new Endpoint(host, Integer.valueOf(port));
    }

  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/websocket")
      .withSockJS();
  }
}
