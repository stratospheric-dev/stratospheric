package dev.stratospheric.todoapp.collaboration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todo")
public class TodoCollaborationController {

  private final TodoCollaborationService todoCollaborationService;

  public TodoCollaborationController(TodoCollaborationService todoCollaborationService) {
    this.todoCollaborationService = todoCollaborationService;
  }

  @PostMapping("/{todoId}/collaborations/{collaboratorId}")
  public String shareTodoWithCollaborator(
    @PathVariable("todoId") Long todoId,
    @PathVariable("collaboratorId") Long collaboratorId,
    RedirectAttributes redirectAttributes
  ) {
    String collaboratorName = todoCollaborationService.shareWithCollaborator(todoId, collaboratorId);

    redirectAttributes.addFlashAttribute("message",
      String.format("You successfully shared your todo with the user %s. " +
        "Once the user accepts the invite, you'll see them as a collaborator on your todo.", collaboratorName));
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }

  @GetMapping("/{todoId}/collaborations/{collaboratorId}/confirm")
  public String confirmCollaboration(
    @PathVariable("todoId") Long todoId,
    @PathVariable("collaboratorId") Long collaboratorId,
    @RequestParam("token") String token,
    RedirectAttributes redirectAttributes
  ) {
    todoCollaborationService.confirmCollaboration(todoId, collaboratorId, token);

    redirectAttributes.addFlashAttribute("message", "You've confirmed that you'd like to collaborate on this todo.");
    redirectAttributes.addFlashAttribute("messageType", "success");

    return "redirect:/dashboard";
  }
}
