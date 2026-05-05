package dev.stratospheric.todoapp.config;

import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

  @ModelAttribute("springBootVersion")
  public String springBootVersion() {
    return SpringBootVersion.getVersion();
  }
}
