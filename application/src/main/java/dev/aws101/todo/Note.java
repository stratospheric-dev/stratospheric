package dev.aws101.todo;

import javax.persistence.*;

@Entity
public class Note {

  @Id
  @GeneratedValue
  private Long id;

  private String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
