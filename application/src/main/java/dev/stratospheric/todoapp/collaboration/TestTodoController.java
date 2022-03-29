package dev.stratospheric.todoapp.collaboration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/testTodo")
@ConditionalOnProperty(prefix = "custom", name = "provide-test-todo-controller", havingValue = "true")
public class TestTodoController {

  private final TodoTestCollaborationService todoTestCollaborationService;

  public TestTodoController(
    TodoTestCollaborationService todoTestCollaborationService
  ) {
    this.todoTestCollaborationService = todoTestCollaborationService;
  }

  @GetMapping("/confirmCollaboration")
  @ResponseBody
  public String confirmCollaboration() {
    String message = todoTestCollaborationService.testConfirmCollaboration();

    return "Done: " + message;
  }
}
