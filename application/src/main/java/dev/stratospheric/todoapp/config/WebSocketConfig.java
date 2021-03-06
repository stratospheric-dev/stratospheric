package dev.stratospheric.todoapp.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final Endpoint websocketEndpoint;

  private final String websocketUsername;

  private final String websocketPassword;

  public WebSocketConfig(
    @Value("${custom.web-socket-relay-endpoint:#{null}}") String websocketRelayEndpoint,
    @Value("${custom.web-socket-relay-username:#{null}}") String websocketUsername,
    @Value("${custom.web-socket-relay-password:#{null}}") String websocketPassword
  ) {
    this.websocketEndpoint = Endpoint.fromEndpointString(websocketRelayEndpoint);
    this.websocketUsername = websocketUsername;
    this.websocketPassword = websocketPassword;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    if (this.websocketEndpoint != null) {
      SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
      ReactorNettyTcpClient<byte[]> tcpClient = new ReactorNettyTcpClient<>(builder ->
        builder
          .host(this.websocketEndpoint.host)
          .port(this.websocketEndpoint.port)
          .secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder))
          .resolver(DefaultAddressResolverGroup.INSTANCE),
        new StompReactorNettyCodec()
      );

      StompBrokerRelayRegistration stompBrokerRelayRegistration = registry
        .enableStompBrokerRelay("/topic")
        .setTcpClient(tcpClient);

      if (websocketUsername != null && websocketPassword != null) {
        stompBrokerRelayRegistration
          .setClientLogin(websocketUsername)
          .setClientPasscode(websocketPassword)
          .setSystemLogin(websocketUsername)
          .setSystemPasscode(websocketPassword);
      }

      if (this.websocketEndpoint.host != null && this.websocketEndpoint.port != null) {
        stompBrokerRelayRegistration
          .setRelayHost(this.websocketEndpoint.host.replace("stomp+ssl://", "")) // see https://stackoverflow.com/questions/49964647/spring-websockets-activemq-convertandsendtouser
          .setRelayPort(this.websocketEndpoint.port);
      }
      if (this.websocketEndpoint.failoverURI != null) {
        stompBrokerRelayRegistration
          .setRelayHost(this.websocketEndpoint.failoverURI);
      }

      registry.setApplicationDestinationPrefixes("/websocketEndpoints");
    }
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
      if (endpoint == null) {
        return null;
      }

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
