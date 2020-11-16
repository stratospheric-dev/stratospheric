package dev.stratospheric.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Profile("!dev")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public AWSCognitoIdentityProvider awsCognitoIdentityProvider(@Value("${cloud.aws.region.static}") String region,
                                                               AWSCredentialsProvider awsCredentialsProvider) {
    return AWSCognitoIdentityProviderAsyncClientBuilder.standard()
      .withCredentials(awsCredentialsProvider)
      .withRegion(region)
      .build();
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
      .authorizeRequests(authorize ->
        authorize
          .mvcMatchers(
            "/",
            "/h2-console/**",
            "/health",
            "/register",
            "/signin",
            "/rocket.svg"
          )
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .logout()
      .logoutSuccessUrl("/");
  }
}
