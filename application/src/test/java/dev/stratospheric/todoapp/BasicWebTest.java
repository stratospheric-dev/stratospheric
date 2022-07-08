package dev.stratospheric.todoapp;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@ExtendWith(ScreenShooterExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BasicWebTest extends AbstractDevIntegrationTest {

  @BeforeAll
  static void setup(@Autowired Environment environment) {
    Configuration.headless = true;
    Configuration.browserCapabilities = new DesiredCapabilities();
    Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions()
      .addArguments("--no-sandbox")
      .addArguments("--disable-dev-shm-usage"));
    Configuration.baseUrl = "http://localhost:" + environment.getProperty("local.server.port", Integer.class);
  }

  @Test
  void shouldLoginAndCreateTodo() {
    open("/");

    login();
    createTodo();
  }

  private void createTodo() {
    $(By.linkText("Dashboard")).click();
    $(By.linkText("Add todo")).click();
    $(By.id("title")).val("Test Todo");
    $(By.id("description")).val("Test Description");
    $(By.id("dueDate")).val("31.12.2099");
    $(By.cssSelector("input[type='submit']")).click();
  }

  private void login() {
    $(By.linkText("Login")).click();
    $("#kc-login").should(Condition.appear);
    $("#username").val("philip");
    $("#password").val("stratospheric");
    $("#kc-login").click();
  }
}
