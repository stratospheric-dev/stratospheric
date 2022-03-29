package dev.stratospheric.todoapp.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

  private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

  private final TodoService todoService;

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";

  public TodoController(
    TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping("/show/{id}")
  public String showView(
    @AuthenticationPrincipal OidcUser user,
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));
    if (!todo.getOwner().getEmail().equals(user.getEmail())) {
      throw new ForbiddenException();
    }

    model.addAttribute("todo", todo);

    return "todo/show";
  }

  @GetMapping("/add")
  public String addView(Model model) {
    model.addAttribute("todo", new Todo());
    model.addAttribute("editMode", EditMode.CREATE);

    return "todo/edit";
  }

  @PostMapping
  public String add(
    @Valid Todo todo,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("todo", todo);
      model.addAttribute("editMode", EditMode.CREATE);

      return "todo/edit";
    }

    todoService.save(todo);

    redirectAttributes.addFlashAttribute("message", "Your new todo has been be saved.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    logger.info("successfully created todo");

    return "redirect:/dashboard";
  }

  @GetMapping("/edit/{id}")
  public String editView(
    @AuthenticationPrincipal OidcUser user,
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));

    if (!todo.getOwner().getEmail().equals(user.getEmail())) {
      throw new ForbiddenException();
    }

    model.addAttribute("todo", todo);
    model.addAttribute("editMode", EditMode.UPDATE);

    return "todo/edit";
  }

  @PostMapping("/update/{id}")
  public String update(
    @AuthenticationPrincipal OidcUser user,
    @PathVariable("id") long id,
    @Valid Todo todo,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) {
      model.addAttribute("todo", todo);
      model.addAttribute("editMode", EditMode.UPDATE);

      return "todo/edit";
    }

    Todo existingTodo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));

    if (!todo.getOwner().getEmail().equals(user.getEmail())) {
      throw new ForbiddenException();
    }

    existingTodo.setTitle(todo.getTitle());
    existingTodo.setDescription(todo.getDescription());
    existingTodo.setPriority(todo.getPriority());
    existingTodo.setDueDate(todo.getDueDate());
    todoService.save(existingTodo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be saved.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    logger.info("successfully updated todo");

    return "redirect:/dashboard";
  }

  @GetMapping("/delete/{id}")
  public String delete(
    @AuthenticationPrincipal OidcUser user,
    @PathVariable("id") long id,
    RedirectAttributes redirectAttributes
  ) {
    Todo todo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));

    if (!todo.getOwner().getEmail().equals(user.getEmail())) {
      throw new ForbiddenException();
    }

    todoService.delete(todo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be deleted.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    logger.info("successfully deleted todo");

    return "redirect:/dashboard";
  }
}
