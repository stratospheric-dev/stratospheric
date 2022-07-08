package dev.stratospheric.todoapp;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ActiveProfiles("dev")
public abstract class AbstractDevIntegrationTest {

  static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9")
    .withDatabaseName("stratospheric")
    .withUsername("stratospheric")
    .withPassword("stratospheric");

  static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.3"))
    .withClasspathResourceMapping("/localstack", "/docker-entrypoint-initaws.d", BindMode.READ_ONLY)
    .withEnv("USE_SINGLE_REGION", "true")
    .withEnv("DEFAULT_REGION", "eu-central-1")
    .withServices(SQS, SES, DYNAMODB)
    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

  static GenericContainer keycloak = new GenericContainer(DockerImageName.parse("quay.io/keycloak/keycloak:18.0.0-legacy"))
    .withExposedPorts(8080)
    .withClasspathResourceMapping("/keycloak", "/tmp", BindMode.READ_ONLY)
    .withEnv("JAVA_OPTS", "-Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/tmp/stratospheric-realm.json")
    .withEnv("DB_VENDOR", "H2")
    .withEnv("KEYCLOAK_USER", "keycloak")
    .withEnv("KEYCLOAK_PASSWORD", "keycloak")
    .waitingFor(Wait.forHttp("/auth").forStatusCode(200));

  static GenericContainer<?> activeMq = new GenericContainer<>(DockerImageName.parse("stratospheric/activemq-docker-image"))
    .withExposedPorts(5672, 61613, 61614, 61616);

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> database.getJdbcUrl());
    registry.add("spring.security.oauth2.client.provider.cognito.issuerUri", () -> "http://localhost:" + keycloak.getMappedPort(8080) + "/auth/realms/stratospheric");
    registry.add("cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(SQS).toString());
    registry.add("cloud.aws.mail.endpoint", () -> localStack.getEndpointOverride(SES).toString());
    registry.add("custom.web-socket-relay-endpoint", () -> "localhost:" + activeMq.getMappedPort(61613));
  }

  static {
    database.start();
    localStack.start();
    keycloak.start();
    activeMq.start();
  }
}
