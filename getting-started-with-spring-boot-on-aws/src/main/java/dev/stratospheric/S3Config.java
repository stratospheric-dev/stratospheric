package dev.stratospheric;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class S3Config {

  @Bean
  public AmazonS3 amazonS3Client() {
    return AmazonS3Client.builder().build();
  }
}
