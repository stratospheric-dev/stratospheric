package dev.stratospheric.todo;

import dev.stratospheric.person.Person;
import dev.stratospheric.person.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;

  public TodoService(
    TodoRepository todoRepository,
    PersonRepository personRepository) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
  }

  public Todo saveNewTodo(Todo todo, String ownerEmail, String ownerName) {

    Person person = personRepository.findByEmail(ownerEmail).orElse(null);

    if (person == null) {
      Person newUser = new Person();
      newUser.setName(ownerName);
      newUser.setEmail(ownerEmail);

      person = personRepository.save(newUser);
    }
    todo.setOwner(person);
    todo.setStatus(Status.OPEN);

    return todoRepository.save(todo);
  }

  public void updateTodo(Todo updatedTodo, long id, String email) {
    Todo existingTodo = getOwnedOrSharedTodo(id, email);

    existingTodo.setTitle(updatedTodo.getTitle());
    existingTodo.setDescription(updatedTodo.getDescription());
    existingTodo.setPriority(updatedTodo.getPriority());
    existingTodo.setDueDate(updatedTodo.getDueDate());

    this.todoRepository.save(existingTodo);
  }

  public void delete(long id, String ownerEmail) {
    this.todoRepository.delete(getOwnedTodo(id, ownerEmail));
  }

  public Todo getOwnedOrSharedTodo(long id, String email) {
    Todo todo = this.todoRepository
      .findById(id)
      .orElseThrow(NotFoundException::new);

    if (userIsNotOwner(email, todo) && userIsNotCollaborator(email, todo)) {
      throw new ForbiddenException();
    }

    return todo;
  }

  private boolean userIsNotCollaborator(String email, Todo todo) {
    return todo.getCollaborators().stream().noneMatch(collaborator -> collaborator.getEmail().equals(email));
  }

  private boolean userIsNotOwner(String email, Todo todo) {
    return !todo.getOwner().getEmail().equals(email);
  }

  private Todo getOwnedTodo(long id, String ownerEmail) {
    Todo todo = this.todoRepository
      .findById(id)
      .orElseThrow(NotFoundException::new);

    if (userIsNotOwner(ownerEmail, todo)) {
      throw new ForbiddenException();
    }

    return todo;
  }
}
