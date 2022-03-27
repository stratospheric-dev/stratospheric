package dev.stratospheric.todoapp.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to start the application local using LocalStack to provide AWS services.
 */
@Configuration
public class LocalAwsConfig {

  @Bean
  @ConditionalOnProperty(prefix = "custom", name = "provide-dynamodb-via-aws", havingValue = "false")
  public AmazonDynamoDB amazonDynamoDB() {
    final AWSStaticCredentialsProvider dummyCredentials
      = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

    final AwsClientBuilder.EndpointConfiguration localEndpoint
      = new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "eu-central-1");

    return AmazonDynamoDBClientBuilder.standard()
      .withCredentials(dummyCredentials)
      .withEndpointConfiguration(localEndpoint)
      .build();
  }
}
