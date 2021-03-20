package dev.stratospheric.todo;

import dev.stratospheric.collaboration.TodoCollaborationRequest;
import dev.stratospheric.person.Person;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 30)
  private String title;

  @Size(max = 100)
  private String description;

  private Priority priority;

  @NotNull
  @Future
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private Person owner;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<Reminder> reminders = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<Note> notes = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<TodoCollaborationRequest> collaborationRequests = new ArrayList<>();

  @ManyToMany
  @JoinTable(name = "todo_collaboration",
    joinColumns = @JoinColumn(name = "todo_id"),
    inverseJoinColumns = @JoinColumn(name = "collaborator_id")
  )
  private List<Person> collaborators = new ArrayList<>();

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

  public void addCollaborator(Person person) {
    this.collaborators.add(person);
    person.getCollaborativeTodos().add(this);
  }

  public void removeCollaborator(Person person) {
    this.collaborators.remove(person);
    person.getCollaborativeTodos().remove(this);
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
