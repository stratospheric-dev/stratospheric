package dev.aws101;

import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import dev.aws101.todo.Todo;
import dev.aws101.todo.TodoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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
  public String getIndex(
    Model model,
    Principal principal
  ) {
    model.addAttribute("indexPageActiveClass", "active");

    Person person = personRepository.findByName("Admin").orElse(null);
    if (principal != null) {
      person = personRepository.findByName(principal.getName()).orElse(null);
    }

    Iterable<Todo> todoList = todoRepository.findAllByOwner(person);
    model.addAttribute("todos", todoList);

    return "index";
  }

}
