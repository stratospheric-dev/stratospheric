package dev.stratospheric.todoapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping(path="/")
  public String getIndex() {
    return "index";
  }

}
