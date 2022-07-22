package dev.stratospheric.todoapp.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AmazonDynamoDBConfig {

  @Bean
  public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB, Environment environment) {

    String environmentName = environment.getProperty("custom.environment");
    String applicationName = environment.getProperty("spring.application.name");

    return new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.builder()
      .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride
        .withTableNamePrefix(environmentName + "-" + applicationName + "-"))
      .build());
  }
}

