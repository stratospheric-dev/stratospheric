package com.aws101;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping
  public String getIndex(Model model) {
    model.addAttribute("message", "Welcome to the TODO application!");
    return "index";
  }

}
