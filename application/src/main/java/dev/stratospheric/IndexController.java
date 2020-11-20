package dev.stratospheric;

import dev.stratospheric.person.PersonRepository;
import dev.stratospheric.todo.TodoRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

  private final PersonRepository personRepository;

  private final TodoRepository todoRepository;

  public IndexController(
    PersonRepository personRepository,
    TodoRepository todoRepository
  ) {
    this.personRepository = personRepository;
    this.todoRepository = todoRepository;
  }

  @GetMapping
  @RequestMapping("/")
  public String getIndex(
    Model model,
    @AuthenticationPrincipal OidcUser user
  ) {
    model.addAttribute("indexPageActiveClass", "active");
    model.addAttribute("collaborators", List.of());

    if (user != null) {
      model.addAttribute("collaborators", personRepository.findByEmailNot(user.getEmail()));
      model.addAttribute("todos", todoRepository.findAllByOwnerEmailOrderByIdAsc(user.getEmail()));
    }

    return "index";
  }

}
