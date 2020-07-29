package dev.aws101.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @GetMapping("/show/{id}")
  public String showView(
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));

    model.addAttribute("todoShowPageActiveClass", "active");
    model.addAttribute("todo", todo);

    return "todo/show";
  }

  @GetMapping("/add")
  public String addView(Model model) {
    model.addAttribute("todoAddPageActiveClass", "active");
    model.addAttribute("todo", new Todo());

    return "todo/add";
  }

  @PostMapping
  public String add(
    @Valid Todo todo,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) {
      model.addAttribute("message", "Your new todo couldn't be saved.");
      model.addAttribute("messageType", "danger");
      model.addAttribute("todo", todo);
      model.addAttribute("errors", result.getFieldErrors());

      return "todo/add";
    }

    todoService.save(todo);

    redirectAttributes.addFlashAttribute("message", "Your new todo has been be saved.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/";
  }

  @GetMapping("/edit/{id}")
  public String editView(
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));

    model.addAttribute("todoEditPageActiveClass", "active");
    model.addAttribute("todo", todo);

    return "todo/edit";
  }

  @PostMapping("/update/{id}")
  public String update(
    @PathVariable("id") long id,
    @Valid Todo todo,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) {
      model.addAttribute("message", "Your todo couldn't be saved.");
      model.addAttribute("messageType", "danger");
      model.addAttribute("todo", todo);
      model.addAttribute("errors", result.getFieldErrors());

      return "todo/update";
    }

    Todo existingTodo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));
    existingTodo.setTitle(todo.getTitle());
    existingTodo.setDescription(todo.getDescription());
    existingTodo.setPriority(todo.getPriority());
    existingTodo.setDueDate(todo.getDueDate());
    todoService.save(existingTodo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be saved.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/";
  }

  @GetMapping("/delete/{id}")
  public String delete(
    @PathVariable("id") long id,
    RedirectAttributes redirectAttributes
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + id));
    todoRepository.delete(todo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be deleted.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/";
  }
}
