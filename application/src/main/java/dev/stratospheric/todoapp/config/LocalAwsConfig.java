package dev.stratospheric.todoapp.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import io.awspring.cloud.core.region.RegionProvider;
import org.springframework.beans.factory.annotation.Value;
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
  // re-using the awspring SQS config for DynamoDB as both connect to LocalStack
  public AmazonDynamoDB amazonDynamoDB(
    @Value("${cloud.aws.sqs.endpoint}") String endpointUrl,
    RegionProvider regionProvider
    ) {
    return AmazonDynamoDBClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar")))
      .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, regionProvider.getRegion().getName()))
      .build();
  }
}
