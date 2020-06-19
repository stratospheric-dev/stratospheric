package dev.aws101;

import java.security.Principal;

import dev.aws101.todo.TodoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  private final TodoRepository todoRepository;

  public IndexController(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  @GetMapping
  public String getIndex(Model model, Principal principal) {

    model.addAttribute("message", "Welcome to the TODO application!");

    if(principal != null) {
      model.addAttribute("todos", todoRepository.findAll());
    }

    return "index";
  }

}
