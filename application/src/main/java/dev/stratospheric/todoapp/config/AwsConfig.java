package dev.stratospheric.todoapp.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@Configuration
public class AwsConfig {

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
  public AWSCognitoIdentityProvider awsCognitoIdentityProvider(
    AwsRegionProvider regionProvider,
    AWSCredentialsProvider awsCredentialsProvider) {
    return AWSCognitoIdentityProviderAsyncClientBuilder.standard()
      .withCredentials(awsCredentialsProvider)
      .withRegion(regionProvider.getRegion().toString())
      .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "provide-dynamodb-via-aws", havingValue = "true")
  public AmazonDynamoDB amazonDynamoDB(
    AwsRegionProvider regionProvider,
    AWSCredentialsProvider awsCredentialsProvider) {
    return AmazonDynamoDBClientBuilder.standard()
      .withCredentials(awsCredentialsProvider)
      .withRegion(regionProvider.getRegion().toString())
      .build();
  }
}
