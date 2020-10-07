package dev.stratospheric.registration;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import dev.stratospheric.person.Person;
import dev.stratospheric.person.PersonRepository;
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

  private final PersonRepository personRepository;

  public RegistrationController(
    RegistrationService registrationService,
    PersonRepository personRepository
  ) {
    this.registrationService = registrationService;
    this.personRepository = personRepository;
  }

  @GetMapping
  public String getRegisterView(Model model) {
    model.addAttribute("registrationPageActiveClass", "active");
    model.addAttribute("registration", new Registration());

    return "register";
  }

  @PostMapping
  public String registerUser(@Valid Registration registration,
                             BindingResult bindingResult,
                             Model model, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("registration", registration);

      return "register";
    }

    if (!registrationService.isValidInvitationCode(registration.getInvitationCode())) {
      model.addAttribute("registration", registration);
      model.addAttribute("message", "Invalid invitation code. Please check it again or contact the person who invited you.");
      model.addAttribute("messageType", "danger");

      return "register";
    }

    try {
      UserType user = registrationService.registerUser(registration);
      Person person = new Person();
      person.setName(user.getUsername());
      for (AttributeType attribute : user.getAttributes()) {
        if (attribute.getName().equals("email")) {
          person.setEmail(attribute.getValue());
        }
      }
      personRepository.save(person);

      redirectAttributes.addFlashAttribute("message",
        "You successfully registered for the Todo App. " +
          "Please check your email inbox for further instructions."
      );
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (InvalidParameterException | UsernameExistsException awsCognitoIdentityProviderException) {
      model.addAttribute("registration", registration);
      model.addAttribute("message", awsCognitoIdentityProviderException.getMessage());
      model.addAttribute("messageType", "danger");

      return "register";
    }

    return "redirect:/register";
  }
}
