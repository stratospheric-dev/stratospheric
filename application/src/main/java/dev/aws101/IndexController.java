package dev.aws101;

import java.security.Principal;

import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import dev.aws101.todo.Todo;
import dev.aws101.todo.TodoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  private final PersonRepository personRepository;

  private final TodoRepository todoRepository;

  @Autowired
  public IndexController(
    PersonRepository personRepository,
    TodoRepository todoRepository
  ) {
    this.personRepository = personRepository;
    this.todoRepository = todoRepository;
  }

  @GetMapping
  public String getIndex(Model model, Principal principal) {

    model.addAttribute("message", "Welcome to the TODO application!");

    Person person = personRepository.findByName("Admin").orElse(null);
    if (principal != null) {
      person = personRepository.findByName(principal.getName()).orElse(null);
    }

    Iterable<Todo> todoList = todoRepository.findAllByOwner(person);
    model.addAttribute("todos", todoList);

    return "index";
  }

}
