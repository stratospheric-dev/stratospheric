package dev.stratospheric.todoapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AwsConfig {

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
  public CognitoIdentityProviderClient cognitoIdentityProviderClient(
    AwsRegionProvider regionProvider,
    AwsCredentialsProvider awsCredentialsProvider) {
    return CognitoIdentityProviderClient.builder()
      .credentialsProvider(awsCredentialsProvider)
      .region(regionProvider.getRegion())
      .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "provide-dynamodb-via-aws", havingValue = "true")
  public DynamoDbEnhancedClient dynamoDbEnhancedClient(
    AwsRegionProvider regionProvider,
    AwsCredentialsProvider awsCredentialsProvider) {
    return DynamoDbEnhancedClient.builder()
      .dynamoDbClient(
        DynamoDbClient
          .builder()
          .credentialsProvider(awsCredentialsProvider)
          .region(regionProvider.getRegion())
          .build()
      )
      .build();
  }
}
