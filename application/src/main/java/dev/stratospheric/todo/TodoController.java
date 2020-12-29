package dev.stratospheric.todo;

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
  private final TodoCollaborationService todoCollaborationService;

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";

  public TodoController(
    TodoRepository todoRepository,
    TodoService todoService,
    TodoCollaborationService todoCollaborationService) {
    this.todoRepository = todoRepository;
    this.todoService = todoService;
    this.todoCollaborationService = todoCollaborationService;
  }

  @GetMapping("/show/{id}")
  public String showView(@PathVariable("id") long id, Model model) {

    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));
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

    return "redirect:/dashboard";
  }

  @GetMapping("/edit/{id}")
  public String editView(
    @PathVariable("id") long id,
    Model model
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));

    model.addAttribute("todo", todo);
    model.addAttribute("editMode", EditMode.UPDATE);

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
      model.addAttribute("todo", todo);
      model.addAttribute("editMode", EditMode.UPDATE);
      return "todo/edit";
    }

    Todo existingTodo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));
    existingTodo.setTitle(todo.getTitle());
    existingTodo.setDescription(todo.getDescription());
    existingTodo.setPriority(todo.getPriority());
    existingTodo.setDueDate(todo.getDueDate());
    todoService.save(existingTodo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be saved.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }

  @GetMapping("/delete/{id}")
  public String delete(
    @PathVariable("id") long id,
    RedirectAttributes redirectAttributes
  ) {
    Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + id));
    todoRepository.delete(todo);

    redirectAttributes.addFlashAttribute("message", "Your todo has been be deleted.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }

  @GetMapping("/{todoId}/share/{collaboratorId}")
  public String shareTodoWithCollaborator(
    @PathVariable("todoId") Long todoId,
    @PathVariable("collaboratorId") Long collaboratorId,
    RedirectAttributes redirectAttributes
  ) {
    String collaboratorName = todoCollaborationService.shareWithCollaborator(todoId, collaboratorId);

    redirectAttributes.addFlashAttribute("message",
      String.format("You successfully shared your todo with the user %s. " +
        "Once the user accepts the invite, you'll see him/her as an collaborator on your todo.", collaboratorName));
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }

  @GetMapping("/{todoId}/confirmCollaboration/{collaboratorId}/{token}")
  public String confirmCollaboration(
    @PathVariable("todoId") Long todoId,
    @PathVariable("collaboratorId") Long collaboratorId,
    @PathVariable("token") String token,
    RedirectAttributes redirectAttributes
  ) {
    String collaboratorName = todoCollaborationService.confirmCollaboration(todoId, collaboratorId, token);

    redirectAttributes.addFlashAttribute("message",
      String.format("You successfully shared your todo with the user %s. " +
        "Once the user accepts the invite, you'll see him/her as an collaborator on your todo.", collaboratorName));
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }
}
