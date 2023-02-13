package dev.stratospheric.config;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
public class AwsConfig {

  @Bean
  public CognitoIdentityProviderClient cognitoIdentityProviderClient(
    AwsRegionProvider regionProvider,
    AwsCredentialsProvider awsCredentialsProvider) {
    return CognitoIdentityProviderClient.builder()
      .credentialsProvider(awsCredentialsProvider)
      .region(regionProvider.getRegion())
      .build();
  }
}
