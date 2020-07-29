package dev.aws101.person;

import dev.aws101.todo.Todo;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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
  @Email
  private String email;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Todo> ownedTodos;

  @ManyToMany(mappedBy = "collaborators")
  private List<Todo> collaborativeTodos;

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
}
