package dev.stratospheric.person;


import dev.stratospheric.collaboration.TodoCollaborationRequest;
import dev.stratospheric.todo.Todo;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  @Column(unique = true)
  private String name;

  @NotEmpty
  @Column(unique = true)
  private String email;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
  private List<Todo> ownedTodos = new ArrayList<>();

  @ManyToMany(mappedBy = "collaborators")
  private List<Todo> collaborativeTodos = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collaborator")
  private List<TodoCollaborationRequest> collaborationRequests = new ArrayList<>();

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
