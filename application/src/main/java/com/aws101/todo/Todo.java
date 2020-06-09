package com.aws101.todo;

import org.springframework.data.annotation.Id;

public class Todo {

  @Id
  private Long id;
  private String title;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
