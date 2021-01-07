package dev.stratospheric.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CognitoOidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  private final String userPoolDomain;
  private final String clientId;

  public CognitoOidcLogoutSuccessHandler(String userPoolDomain, String clientId) {
    this.userPoolDomain = userPoolDomain;
    this.clientId = clientId;
  }

  @Override
  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) {

    UriComponents baseUrl = UriComponentsBuilder
      .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
      .replacePath(request.getContextPath())
      .replaceQuery(null)
      .fragment(null)
      .build();

    return UriComponentsBuilder
      .fromUri(URI.create(userPoolDomain + "/logout"))
      .queryParam("client_id", clientId)
      .queryParam("logout_uri", baseUrl)
      .encode(StandardCharsets.UTF_8)
      .build()
      .toUriString();
  }
}
