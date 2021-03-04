package dev.stratospheric.dashboard;


import dev.stratospheric.todo.Todo;

import java.time.LocalDate;

public class TodoDto {

  private Long id;
  private String title;
  private int amountOfCollaborators;
  private int amountOfCollaborationRequests;
  private LocalDate dueDate;
  private boolean isCollaboration;

  public TodoDto(Todo todo, boolean isCollaboration) {
    this.id = todo.getId();
    this.title = todo.getTitle();
    this.amountOfCollaborationRequests = todo.getCollaborationRequests().size();
    this.amountOfCollaborators = todo.getCollaborators().size();
    this.dueDate = todo.getDueDate();
    this.isCollaboration = isCollaboration;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public int getAmountOfCollaborators() {
    return amountOfCollaborators;
  }

  public int getAmountOfCollaborationRequests() {
    return amountOfCollaborationRequests;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public boolean isCollaboration() {
    return isCollaboration;
  }
}
