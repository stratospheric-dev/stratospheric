package dev.stratospheric.todoapp.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class WebSecurityConfig {

  private final LogoutSuccessHandler logoutSuccessHandler;

  public WebSecurityConfig(LogoutSuccessHandler logoutSuccessHandler) {
    this.logoutSuccessHandler = logoutSuccessHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity
      .csrf()
      .ignoringRequestMatchers(
        "/stratospheric-todo-updates/**",
        "/websocket/**"
      )
      .and()
      .oauth2Login()
      .and()
      .authorizeHttpRequests()
      .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .requestMatchers("/", "/register").permitAll()
      .anyRequest().authenticated()
      .and()
      .logout()
      .logoutSuccessHandler(logoutSuccessHandler);

    return httpSecurity.build();
  }
}
