package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.SpringBootApplicationStack;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

import java.util.Objects;

public class CdkApp {

  public static void main(final String[] args) {
    App app = new App();

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Objects.requireNonNull(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Objects.requireNonNull(region, "context variable 'region' must not be null");

    new SpringBootApplicationStack(
      app,
      "SpringBootApplication",
      makeEnv(accountId, region),
      "docker.io/stratospheric/todo-app-v1:latest");

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }
}
