package dev.stratospheric.todoapp.collaboration;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.todo.Todo;

import javax.persistence.*;

@Entity
public class TodoCollaborationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  @ManyToOne
  @JoinColumn(name = "collaborator_id")
  private Person collaborator;

  @ManyToOne
  private Todo todo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Person getCollaborator() {
    return collaborator;
  }

  public void setCollaborator(Person collaborator) {
    this.collaborator = collaborator;
  }

  public Todo getTodo() {
    return todo;
  }

  public void setTodo(Todo todo) {
    this.todo = todo;
  }

  @Override
  public String toString() {
    return "TodoCollaborationRequest{" +
      "todoId=" + todo.getId() +
      ", todoTitle='" + todo.getTitle() + '\'' +
      ", todoDescription='" + todo.getDescription() + '\'' +
      ", todoPriority=" + todo.getPriority() +
      ", collaboratorId=" + collaborator.getId() +
      ", collaboratorName='" + collaborator.getName() + '\'' +
      ", collaboratorEmail='" + collaborator.getEmail() + '\'' +
      '}';
  }
}
