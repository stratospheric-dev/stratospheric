package dev.stratospheric.todoapp.config;

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
    /* Disabled until we can connect to MQ logs messages about failing connections are increasing
    if (this.websocketEndpoint != null) {
      StompBrokerRelayRegistration stompBrokerRelayRegistration = config
        .enableStompBrokerRelay("/topic");

      if (this.websocketEndpoint.host != null && this.websocketEndpoint.port != null) {
        stompBrokerRelayRegistration
          .setRelayHost(this.websocketEndpoint.host)
          .setRelayPort(this.websocketEndpoint.port);
      }
      if (this.websocketEndpoint.failoverURI != null) {
        stompBrokerRelayRegistration
          .setRelayHost(this.websocketEndpoint.failoverURI);
      }

      config.setApplicationDestinationPrefixes("/websocketEndpoints");
    }
    */

    config.setApplicationDestinationPrefixes("/websocketEndpoints");
    config.enableSimpleBroker("/topic");
  }

  private static class Endpoint {
    final String host;
    final Integer port;
    final String failoverURI;

    public Endpoint(String host, int port) {
      this.host = host;
      this.port = port;
      this.failoverURI = null;
    }

    public Endpoint(String failoverURI) {
      this.host = null;
      this.port = null;
      this.failoverURI = failoverURI;
    }

    static Endpoint fromEndpointString(String endpoint) {
      String host;
      String port;
      String failoverURI;

      Pattern hostAndPortPattern = Pattern.compile("^(.*):([0-9]+$)");
      Matcher hostAndPortMatcher = hostAndPortPattern.matcher(endpoint);

      if (hostAndPortMatcher.matches()) {
        host = hostAndPortMatcher.group(1);
        port = hostAndPortMatcher.group(2);

        return new Endpoint(host, Integer.parseInt(port));
      }

      Pattern failoverURIPattern = Pattern.compile("^(failover:\\(.*\\))");
      Matcher failoverURIMatcher = failoverURIPattern.matcher(endpoint);
      if (failoverURIMatcher.matches()) {
        failoverURI = failoverURIMatcher.group(0);

        return new Endpoint(failoverURI);
      }

      if (!(hostAndPortMatcher.matches() || failoverURIMatcher.matches())) {
        throw new IllegalStateException(String.format("Invalid endpoint string (must either consist of hostname and port or a failover URI): %s", endpoint));
      }

      return null;
    }
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/websocket")
      .withSockJS();
  }
}
