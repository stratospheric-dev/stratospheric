package dev.aws101.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsyncClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AwsConfig {

  @Value("${cloud.aws.credentials.accessKey}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secretKey}")
  private String secretKey;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean
  @ConditionalOnProperty(value = "custom.security.enabled", havingValue = "true", matchIfMissing = true)
  public AWSCognitoIdentityProvider awsCognitoIdentityProvider(AWSCredentialsProvider awsCredentialsProvider) {
    return AWSCognitoIdentityProviderAsyncClientBuilder.standard()
      .withCredentials(awsCredentialsProvider)
      .withRegion(region)
      .build();
  }

  @Bean
  public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    return new QueueMessagingTemplate(amazonSQSAsync);
  }

  @Bean
  public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
    return new NotificationMessagingTemplate(amazonSNS);
  }

  @Bean
  public AmazonSNS amazonSNS() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

    return AmazonSNSClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
      .build();
  }
}
