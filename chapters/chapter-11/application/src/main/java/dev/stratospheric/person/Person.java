package dev.stratospheric.person;

import dev.stratospheric.collaboration.TodoCollaborationRequest;
import dev.stratospheric.todo.Todo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String name;

  @NotEmpty
  @Column(unique = true)
  private String email;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
  private List<Todo> ownedTodos;

  @ManyToMany(mappedBy = "collaborators")
  private List<Todo> collaborativeTodos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collaborator")
  private List<TodoCollaborationRequest> collaborationRequests;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Todo> getOwnedTodos() {
    return ownedTodos;
  }

  public void setOwnedTodos(List<Todo> ownedTodos) {
    this.ownedTodos = ownedTodos;
  }

  public List<Todo> getCollaborativeTodos() {
    return collaborativeTodos;
  }

  public void setCollaborativeTodos(List<Todo> collaborativeTodos) {
    this.collaborativeTodos = collaborativeTodos;
  }

  public List<TodoCollaborationRequest> getCollaborationRequests() {
    return collaborationRequests;
  }

  public void setCollaborationRequests(List<TodoCollaborationRequest> collaborationRequests) {
    this.collaborationRequests = collaborationRequests;
  }
}
