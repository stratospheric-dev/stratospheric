package dev.aws101.collaboration;

import dev.aws101.todo.Priority;

import java.time.LocalDate;

public class TodoCollaborationRequest {

  private Long todoId;
  private String todoTitle;
  private String todoDescription;
  private Priority todoPriority;
  private Long collaboratorId;
  private String collaboratorName;
  private String collaboratorEmail;

  public Long getTodoId() {
    return todoId;
  }

  public void setTodoId(Long todoId) {
    this.todoId = todoId;
  }

  public String getTodoTitle() {
    return todoTitle;
  }

  public void setTodoTitle(String todoTitle) {
    this.todoTitle = todoTitle;
  }

  public String getTodoDescription() {
    return todoDescription;
  }

  public void setTodoDescription(String todoDescription) {
    this.todoDescription = todoDescription;
  }

  public Priority getTodoPriority() {
    return todoPriority;
  }

  public void setTodoPriority(Priority todoPriority) {
    this.todoPriority = todoPriority;
  }

  public Long getCollaboratorId() {
    return collaboratorId;
  }

  public void setCollaboratorId(Long collaboratorId) {
    this.collaboratorId = collaboratorId;
  }

  public String getCollaboratorName() {
    return collaboratorName;
  }

  public void setCollaboratorName(String collaboratorName) {
    this.collaboratorName = collaboratorName;
  }

  public String getCollaboratorEmail() {
    return collaboratorEmail;
  }

  public void setCollaboratorEmail(String collaboratorEmail) {
    this.collaboratorEmail = collaboratorEmail;
  }

  @Override
  public String toString() {
    return "TodoCollaborationRequest{" +
      "todoId=" + todoId +
      ", todoTitle='" + todoTitle + '\'' +
      ", todoDescription='" + todoDescription + '\'' +
      ", todoPriority=" + todoPriority +
      ", collaboratorId=" + collaboratorId +
      ", collaboratorName='" + collaboratorName + '\'' +
      ", collaboratorEmail='" + collaboratorEmail + '\'' +
      '}';
  }
}
