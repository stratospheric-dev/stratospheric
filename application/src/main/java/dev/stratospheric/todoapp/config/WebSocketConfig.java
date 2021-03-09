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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
    this.websocketEndpoint = Endpoint.fromEndpointString("b-fc191f73-2da3-4e2e-ad4d-c9c3793f7385-1.mq.eu-central-1.amazonaws.com:61614");
    this.websocketUsername = websocketUsername;
    this.websocketPassword = websocketPassword;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    if (this.websocketEndpoint != null) {
      ReactorNettyTcpClient<byte[]> tcpClient = createTcpClient(this.websocketEndpoint);

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

      registry.setApplicationDestinationPrefixes("/websocketEndpoints");
    }
  }

  private ReactorNettyTcpClient<byte[]> createTcpClient(Endpoint endpoint) {
    SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

    if (endpoint.activeStandbyHosts != null && !endpoint.activeStandbyHosts.isEmpty()) {
      final List<InetSocketAddress> addressList = new ArrayList<>();
      for (String hostURI : endpoint.activeStandbyHosts) {
        String[] hostAndPort = hostURI.split(":");
        addressList.add(new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
      }
      final RoundRobinList<InetSocketAddress> addresses = new RoundRobinList<>(addressList);

      return new ReactorNettyTcpClient<>(builder ->
        builder
          .remoteAddress(addresses::get)
          .secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder))
          .resolver(DefaultAddressResolverGroup.INSTANCE),
        new StompReactorNettyCodec()
      );
    }

    return new ReactorNettyTcpClient<>(builder ->
      builder
        .host(this.websocketEndpoint.host)
        .port(this.websocketEndpoint.port)
        .secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder))
        .resolver(DefaultAddressResolverGroup.INSTANCE),
      new StompReactorNettyCodec()
    );
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/websocket")
      .withSockJS();
  }

  private static class Endpoint {
    final String host;
    final Integer port;
    final List<String> activeStandbyHosts;

    public Endpoint(String host, int port) {
      this.host = host;
      this.port = port;
      this.activeStandbyHosts = null;
    }

    public Endpoint(List<String> activeStandbyHosts) {
      this.host = null;
      this.port = null;
      this.activeStandbyHosts = activeStandbyHosts;
    }

    static Endpoint fromEndpointString(String endpoint) {
      if (endpoint == null) {
        return null;
      }

      String host;
      String port;

      Pattern hostAndPortPattern = Pattern.compile("^(.*):([0-9]+$)");
      Matcher hostAndPortMatcher = hostAndPortPattern.matcher(endpoint);

      if (hostAndPortMatcher.matches()) {
        host = hostAndPortMatcher
          .group(1)
          .replace("stomp+ssl://", ""); // see https://stackoverflow.com/questions/49964647/spring-websockets-activemq-convertandsendtouser
        port = hostAndPortMatcher.group(2);

        return new Endpoint(host, Integer.parseInt(port));
      }

      Pattern failoverURIPattern = Pattern.compile("^(failover:\\(.*\\))");
      Matcher failoverURIMatcher = failoverURIPattern.matcher(endpoint);
      if (failoverURIMatcher.matches()) {
        Pattern hostPattern = Pattern.compile("//(.+?)[,)]{1}");
        Matcher hostMatcher = hostPattern.matcher(endpoint);
        List<String> activeStandbyHosts = new ArrayList<>();
        while (hostMatcher.find()) {
          activeStandbyHosts.add(hostMatcher.group(1));
        }

        return new Endpoint(activeStandbyHosts);
      }

      if (!(hostAndPortMatcher.matches() || failoverURIMatcher.matches())) {
        throw new IllegalStateException(String.format("Invalid endpoint string (must either consist of hostname and port or a failover URI): %s", endpoint));
      }

      return null;
    }
  }

  private static class RoundRobinList<T> {

    private Iterator<T> iterator;
    private final Collection<T> elements;

    public RoundRobinList(Collection<T> elements) {
      this.elements = elements;
      iterator = this.elements.iterator();
    }

    public synchronized T get() {
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        iterator = elements.iterator();
        return iterator.next();
      }
    }

    public int size() {
      return elements.size();
    }
  }
}
