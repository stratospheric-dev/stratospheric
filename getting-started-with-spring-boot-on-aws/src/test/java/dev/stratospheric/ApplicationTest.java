package dev.stratospheric;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApplicationTest {

  private static final String SAMPLE_BUCKET = "sample-bucket";
  private static final String SAMPLE_QUEUE = "sample-queue";

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @LocalServerPort
  private int port;

  @Container
  static LocalStackContainer localStack =
    new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.7.0"))
      .withServices(SQS, S3, SSM);

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", SAMPLE_QUEUE);
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
  void contextLoads() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/"))
      .GET()
      .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode())
      .isEqualTo(200);
  }
}
