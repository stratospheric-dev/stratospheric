package dev.aws101;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static dev.aws101.TodoApplicationTests.localStack;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@TestConfiguration
public class AwsTestConfig {

  @Bean
  public AmazonSQSAsync amazonSQS() {
    return AmazonSQSAsyncClientBuilder.standard()
      .withCredentials(localStack.getDefaultCredentialsProvider())
      .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
      .build();
  }
}
