package dev.stratospheric.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .and()
      .oauth2Login()
      .and()
      .authorizeRequests()
      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .mvcMatchers("/", "/health", "/register").permitAll()
      .and()
      .logout()
      .logoutSuccessUrl("/");
  }
}
