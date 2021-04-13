package dev.stratospheric.todoapp.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "dev.stratospheric.todoapp.tracing")
public class DynamoDBConfig {
}
