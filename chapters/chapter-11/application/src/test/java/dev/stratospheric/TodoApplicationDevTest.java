package dev.stratospheric;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("dev")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoApplicationDevTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    assertNotNull(applicationContext);
  }

}
