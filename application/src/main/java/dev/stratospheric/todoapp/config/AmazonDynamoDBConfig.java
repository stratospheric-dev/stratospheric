package dev.stratospheric.todoapp.config;

import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Locale;

@Configuration
public class AmazonDynamoDBConfig {

  @Bean
  public DynamoDbTableNameResolver dynamoDbTableNameResolver(Environment environment) {
    String environmentName = environment.getProperty("custom.environment");
    String applicationName = environment.getProperty("spring.application.name");

    return new DynamoDbTableNameResolver() {
      @Override
      public <T> String resolve(Class<T> clazz) {
        String className = clazz.getSimpleName().replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase(Locale.ROOT);
        return environmentName + "-" + applicationName + "-" + className;
      }
    };
  }
}

