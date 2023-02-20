package dev.stratospheric;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApplicationTest {

  private static final String SAMPLE_BUCKET = "sample-bucket";
  private static final String SAMPLE_QUEUE = "sample-queue";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Container
  static LocalStackContainer localStack =
    new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.0"))
      .withServices(SQS, S3, SSM);

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", SAMPLE_BUCKET);
    localStack.execInContainer("awslocal", "s3", "mb", "s3://" + SAMPLE_BUCKET);
  }

  @DynamicPropertySource
  static void overrideConfiguration(DynamicPropertyRegistry registry) {
    registry.add("custom.bucket-name", () -> SAMPLE_BUCKET);
    registry.add("custom.sqs-queue-name", () -> SAMPLE_QUEUE);
    registry.add("spring.cloud.aws.endpoint", () -> localStack.getEndpointOverride(S3));
    registry.add("spring.cloud.aws.region.static", localStack::getRegion);
    registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
    registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
  }

  @Test
  void contextLoads() {
    ResponseEntity<String> result = this.testRestTemplate
      .getForEntity("/", String.class);

    assertThat(result.getStatusCode().value())
      .isEqualTo(200);
  }
}

