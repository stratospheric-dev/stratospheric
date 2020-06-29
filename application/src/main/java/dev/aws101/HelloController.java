package dev.aws101;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/hello")
public class HelloController {

  @GetMapping
  public String sayHello() {
    return "Hello World v2! " + LocalDateTime.now().toString();
  }
}
