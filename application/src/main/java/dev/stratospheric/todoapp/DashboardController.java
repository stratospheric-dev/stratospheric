package dev.stratospheric.todoapp;

import dev.stratospheric.todoapp.person.PersonRepository;
import dev.stratospheric.todoapp.todo.TodoRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

  private final PersonRepository personRepository;
  private final TodoRepository todoRepository;

  public DashboardController(
    PersonRepository personRepository,
    TodoRepository todoRepository
  ) {
    this.personRepository = personRepository;
    this.todoRepository = todoRepository;
  }

  @GetMapping
  public String getDashboard(Model model, @AuthenticationPrincipal OidcUser user) {
    model.addAttribute("collaborators", List.of());

    if (user != null) {
      model.addAttribute("collaborators", personRepository.findByEmailNot(user.getEmail()));
      model.addAttribute("todos", todoRepository.findAllByOwnerEmailOrderByIdAsc(user.getEmail()));
    }

    return "dashboard";
  }
}
