package dev.stratospheric.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnProperty(value = "custom.security.enabled", havingValue = "false")
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .ignoringAntMatchers(
        "/stratospheric-todo-updates/**",
        "/websocket/**"
      )
      .and()
      .authorizeRequests(authorize ->
        authorize
          .mvcMatchers("/**")
          .permitAll()
      );
  }
}
