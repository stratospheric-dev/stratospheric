package dev.stratospheric.todoapp.collaboration;

import dev.stratospheric.todoapp.todo.Priority;

public class TodoCollaborationNotification {

  private String collaboratorEmail;
  private String collaboratorName;
  private Long collaboratorId;

  private String todoTitle;
  private String todoDescription;
  private Priority todoPriority;
  private Long todoId;

  private String token;

  public TodoCollaborationNotification() {
  }

  public TodoCollaborationNotification(TodoCollaborationRequest todoCollaborationRequest) {
    this.collaboratorEmail = todoCollaborationRequest.getCollaborator().getEmail();
    this.collaboratorName = todoCollaborationRequest.getCollaborator().getName();
    this.collaboratorId = todoCollaborationRequest.getCollaborator().getId();
    this.todoTitle = todoCollaborationRequest.getTodo().getTitle();
    this.todoDescription = todoCollaborationRequest.getTodo().getDescription();
    this.todoId = todoCollaborationRequest.getTodo().getId();
    this.todoPriority = todoCollaborationRequest.getTodo().getPriority();
    this.token = todoCollaborationRequest.getToken();
  }

  public String getCollaboratorEmail() {
    return collaboratorEmail;
  }

  public void setCollaboratorEmail(String collaboratorEmail) {
    this.collaboratorEmail = collaboratorEmail;
  }

  public String getCollaboratorName() {
    return collaboratorName;
  }

  public void setCollaboratorName(String collaboratorName) {
    this.collaboratorName = collaboratorName;
  }

  public Long getCollaboratorId() {
    return collaboratorId;
  }

  public void setCollaboratorId(Long collaboratorId) {
    this.collaboratorId = collaboratorId;
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

  public Long getTodoId() {
    return todoId;
  }

  public void setTodoId(Long todoId) {
    this.todoId = todoId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "TodoCollaborationNotification{" +
      "collaboratorEmail='" + collaboratorEmail + '\'' +
      ", collaboratorName='" + collaboratorName + '\'' +
      ", collaboratorId=" + collaboratorId +
      ", todoTitle='" + todoTitle + '\'' +
      ", todoDescription='" + todoDescription + '\'' +
      ", todoPriority=" + todoPriority +
      ", todoId=" + todoId +
      ", token='" + token + '\'' +
      '}';
  }
}
