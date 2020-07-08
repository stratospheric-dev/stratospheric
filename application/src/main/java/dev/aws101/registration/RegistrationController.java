package dev.aws101.registration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @GetMapping
  public String getRegisterView(Model model) {
    model.addAttribute("registration", new Registration());
    return "register";
  }

  @PostMapping
  public String registerUser(@Valid Registration registration,
                             BindingResult bindingResult,
                             Model model, RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return "register";
    }

    // TODO: Move this also to Bean Validation like Password
    if (!registrationService.isValidInvitationCode(registration.getInvitationCode())) {
      model.addAttribute("message", "Invalid invitation code. Please check it again or contact the person who invited you.");
      model.addAttribute("messageType", "danger");
      return "register";
    }

    registrationService.registerUser(registration);
    redirectAttributes.addFlashAttribute("message", "You successfully registered for the Todo App. " +
      "Go check your E-Mail inbox for further instructions.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/register";
  }

}
