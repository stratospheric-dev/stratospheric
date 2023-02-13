package dev.stratospheric.todoapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Configuration to start the application local using LocalStack to provide AWS services.
 */
@Configuration
public class LocalAwsConfig {

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "provide-dynamodb-via-aws", havingValue = "false")
  public DynamoDbEnhancedClient dynamoDbEnhancedClient(
    AwsRegionProvider regionProvider,
    @Value("${spring.cloud.aws.endpoint}") String endpointUrl) {
    return DynamoDbEnhancedClient.builder()
      .dynamoDbClient(
        DynamoDbClient
          .builder()
          .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create("foo", "bar"))
          )
          .endpointOverride(java.net.URI.create(endpointUrl))
          .region(regionProvider.getRegion())
          .build()
      )
      .build();
  }
}
