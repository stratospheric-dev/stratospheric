package dev.stratospheric;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApplicationTest {

  private static final String SAMPLE_BUCKET = "sample-bucket";
  private static final String SAMPLE_QUEUE = "sample-queue";

  private final RestClient restClient = RestClient.create();

  @LocalServerPort
  private int port;

  @Container
  static LocalStackContainer localStack =
    new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.7.0"))
      .withServices("sqs", "s3", "ssm");

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", SAMPLE_QUEUE);
    localStack.execInContainer("awslocal", "s3", "mb", "s3://" + SAMPLE_BUCKET);
  }

  @DynamicPropertySource
  static void overrideConfiguration(DynamicPropertyRegistry registry) {
    registry.add("custom.bucket-name", () -> SAMPLE_BUCKET);
    registry.add("custom.sqs-queue-name", () -> SAMPLE_QUEUE);
    registry.add("spring.cloud.aws.endpoint", () -> localStack.getEndpoint());
    registry.add("spring.cloud.aws.region.static", localStack::getRegion);
    registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
    registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
  }

  @Test
  void contextLoads() {
    String response = restClient.get()
      .uri("http://localhost:" + port + "/")
      .retrieve()
      .body(String.class);

    assertThat(response)
      .as("Response body should not be empty")
      .isNotEmpty();
  }
}
