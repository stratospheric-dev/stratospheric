package dev.stratospheric.todoapp.dashboard;

public class CollaboratorDto {

  private final Long id;
  private final String name;

  public CollaboratorDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
