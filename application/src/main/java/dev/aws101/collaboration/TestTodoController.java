package dev.aws101.collaboration;

import dev.aws101.todo.TodoService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/testTodo")
@Profile("dev")
public class TestTodoController {

  private final TodoService todoService;

  public TestTodoController(
    TodoService todoService
  ) {
    this.todoService = todoService;
  }

  @GetMapping("/confirmCollaboration")
  @ResponseBody
  public String confirmCollaboration() {
    String subject = todoService.testConfirmCollaboration();

    return "Done: " + subject;
  }
}
