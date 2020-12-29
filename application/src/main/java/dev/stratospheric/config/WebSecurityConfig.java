package dev.stratospheric.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ClientRegistrationRepository clientRegistrationRepository;

  public WebSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
    OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    successHandler.setPostLogoutRedirectUri("{baseUrl}");
    return successHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .ignoringAntMatchers(
        "/stratospheric-todo-updates/**",
        "/websocket/**"
      )
      .and()
      .oauth2Login()
      .and()
      .authorizeRequests()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .mvcMatchers("/", "/health", "/register").permitAll()
      .and()
      .logout()
      .logoutSuccessHandler(oidcLogoutSuccessHandler());
  }
}
