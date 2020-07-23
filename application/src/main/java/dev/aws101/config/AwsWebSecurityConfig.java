package dev.aws101.config;

import org.springframework.cloud.aws.context.annotation.ConditionalOnAwsCloudEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnAwsCloudEnvironment
public class AwsWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .and()
      .oauth2Login()
      .and()
      .authorizeRequests(authorize ->
        authorize
          .mvcMatchers("/", "/h2-console/**", "/hello", "/register", "/signin")
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .logout()
      .logoutSuccessUrl("/");
  }
}
