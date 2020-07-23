package dev.aws101.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/todo")
public class TodoController {

  private final TodoRepository todoRepository;

  private final TodoService todoService;

  @Autowired
  public TodoController(
    TodoRepository todoRepository,
    TodoService todoService
  ) {
    this.todoRepository = todoRepository;
    this.todoService = todoService;
  }

  @GetMapping("/add")
  public String showAddView(Model model) {
    model.addAttribute("todo", new Todo());

    return "todo/add";
  }

  @PostMapping
  public String add(
    @Valid Todo todo,
    BindingResult result,
    Model model
  ) {
    System.out.println(result.getAllErrors().get(0).getDefaultMessage());
    if (result.hasErrors()) {
      model.addAttribute("message", "Your new todo couldn't be saved.");
      model.addAttribute("todo", todo);

      return "todo/add";
    }

    todoService.save(todo);

    model.addAttribute("message", "Your new todo has been be saved.");

    return "redirect:/";
  }

  @GetMapping("/edit/{id}")
  public String showUpdateView(
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));

    model.addAttribute("todo", todo);

    return "todo/update";
  }

  @PutMapping("/{id}")
  public String update(
    @PathVariable("id") long id,
    @Valid Todo todo,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) {
      model.addAttribute("message", "Your todo couldn't be saved.");
      model.addAttribute("todo", todo);

      return "todo/update";
    }

    todoService.save(todo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be saved.");

    return "redirect:/";
  }

  @DeleteMapping("/{id}")
  public String delete(
    @PathVariable("id") long id,
    RedirectAttributes redirectAttributes
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));
    todoRepository.delete(todo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be deleted.");

    return "redirect:/";
  }
}
