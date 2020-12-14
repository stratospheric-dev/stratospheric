package dev.stratospheric;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("dev")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TodoApplicationDevTest {

  public static DockerComposeContainer<?> environment =
    new DockerComposeContainer<>(new File("docker-compose.yml"))
      .withExposedService("keycloak_1", 8080, Wait.forHttp("/auth").forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(30)));

  static {
    environment.start();
  }

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    assertNotNull(applicationContext);
  }

}
