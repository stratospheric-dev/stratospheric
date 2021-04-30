package dev.stratospheric.todoapp.collaboration;

import io.micrometer.core.annotation.Timed;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

  @Timed(
    value = "stratospheric.collaboration.sharing",
    description = "Measure the time how long it takes to share a todo"
  )
  @PostMapping("/{todoId}/collaborations/{collaboratorId}")
  public String shareTodoWithCollaborator(
    @PathVariable("todoId") Long todoId,
    @PathVariable("collaboratorId") Long collaboratorId,
    @AuthenticationPrincipal OidcUser user,
    RedirectAttributes redirectAttributes
  ) {
    String collaboratorName = todoCollaborationService.shareWithCollaborator(user.getEmail(), todoId, collaboratorId);

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
    @AuthenticationPrincipal OidcUser user,
    RedirectAttributes redirectAttributes
  ) {
    if (todoCollaborationService.confirmCollaboration(user.getEmail(), todoId, collaboratorId, token)) {
      redirectAttributes.addFlashAttribute("message", "You've confirmed that you'd like to collaborate on this todo.");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } else {
      redirectAttributes.addFlashAttribute("message", "Invalid collaboration request.");
      redirectAttributes.addFlashAttribute("messageType", "danger");
    }

    return "redirect:/dashboard";
  }
}
