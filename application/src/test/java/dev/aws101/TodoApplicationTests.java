package dev.aws101;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest
@Testcontainers
@Import(AwsTestConfig.class)
class TodoApplicationTests {

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(LocalStackContainer.VERSION)
    .withServices(SQS, SNS)
    .withEnv("DEFAULT_REGION", "eu-central-1");

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "test-todo-sharing");
    localStack.execInContainer("awslocal", "sns", "create-topic", "--name", "test-todo-updates");
  }

  @Test
  void contextLoads() {
  }

}
