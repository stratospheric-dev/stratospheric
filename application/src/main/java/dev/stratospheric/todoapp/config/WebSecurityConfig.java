package dev.stratospheric.todoapp.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final LogoutSuccessHandler logoutSuccessHandler;

  public WebSecurityConfig(LogoutSuccessHandler logoutSuccessHandler) {
    this.logoutSuccessHandler = logoutSuccessHandler;
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
      .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .mvcMatchers("/", "/register").permitAll()
      .anyRequest().authenticated()
      .and()
      .logout()
      .logoutSuccessHandler(logoutSuccessHandler);
  }
}
