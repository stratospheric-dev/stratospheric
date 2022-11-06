package dev.stratospheric.todoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class MessagingTemplateConfig {

  @Bean
  public SqsClient sqsClient() {
    return SqsClient.create();
  }
}
