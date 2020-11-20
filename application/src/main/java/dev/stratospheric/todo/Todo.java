package dev.stratospheric.todo;

import dev.stratospheric.collaboration.TodoCollaborationRequest;
import dev.stratospheric.person.Person;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String title;

  private String description;

  private Priority priority;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private Person owner;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<Reminder> reminders;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<Note> notes;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<TodoCollaborationRequest> collaborationRequests;

  @ManyToMany
  @JoinTable(name = "todo_collaboration",
    joinColumns = @JoinColumn(name = "todo_id"),
    inverseJoinColumns = @JoinColumn(name = "collaborator_id")
  )
  private List<Person> collaborators;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Person getOwner() {
    return owner;
  }

  public void setOwner(Person owner) {
    this.owner = owner;
  }

  public List<Reminder> getReminders() {
    return reminders;
  }

  public void setReminders(List<Reminder> reminders) {
    this.reminders = reminders;
  }

  public List<Note> getNotes() {
    return notes;
  }

  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  public List<TodoCollaborationRequest> getCollaborationRequests() {
    return collaborationRequests;
  }

  public void setCollaborationRequests(List<TodoCollaborationRequest> collaborationRequests) {
    this.collaborationRequests = collaborationRequests;
  }

  public List<Person> getCollaborators() {
    return collaborators;
  }

  public void setCollaborators(List<Person> collaborators) {
    this.collaborators = collaborators;
  }

  @Override
  public String toString() {
    return "Todo{" +
      "id=" + id +
      ", title='" + title + '\'' +
      ", description='" + description + '\'' +
      ", priority=" + priority +
      ", dueDate=" + dueDate +
      ", status=" + status +
      ", owner=" + owner +
      '}';
  }
}
