package dev.aws101.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/todo")
public class TodoController {

  private final TodoRepository todoRepository;

  @Autowired
  public TodoController(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  @GetMapping("/add")
  public String showAddView(Todo todo) {
    return "todo/add";
  }

  @PostMapping
  public String add(@Valid Todo todo, BindingResult result, Model model) {
    if (result.hasErrors()) {
      return "todo/add";
    }

    todoRepository.save(todo);
    model.addAttribute("todos", todoRepository.findAll());

    return "redirect:todo/index";
  }

  @GetMapping("/edit/{id}")
  public String showUpdateForm(@PathVariable("id") long id, Model model) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));
    model.addAttribute("todo", todo);

    return "todo/update";
  }

  @PutMapping("/{id}")
  public String updateUser(@PathVariable("id") long id, @Valid Todo todo, BindingResult result, Model model) {
    if (result.hasErrors()) {
      todo.setId(id);

      return "todo/update";
    }

    todoRepository.save(todo);
    model.addAttribute("todos", todoRepository.findAll());

    return "redirect:todo/index";
  }

  @DeleteMapping("/{id}")
  public String deleteUser(@PathVariable("id") long id, Model model) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));
    todoRepository.delete(todo);

    model.addAttribute("todos", todoRepository.findAll());

    return "redirect:todo/index";
  }
}
