package dev.aws101;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

  @GetMapping
  public String getRegisterView(Model model) {
    model.addAttribute("registration", new Registration());
    return "register";
  }

  @PostMapping
  public String registerUser(@ModelAttribute Registration registration, Model model) {
    System.out.println(registration);
    return "register";
  }

}
