package dev.stratospheric.todo;

import java.util.Optional;

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

  public Optional<Todo> findById(Long id) {
    return this.todoRepository.findById(id);
  }

  public void updateTodo(Todo updatedTodo, long id, String ownerEmail) {
    Todo existingTodo = getOwnedTodo(id, ownerEmail);

    existingTodo.setTitle(updatedTodo.getTitle());
    existingTodo.setDescription(updatedTodo.getDescription());
    existingTodo.setPriority(updatedTodo.getPriority());
    existingTodo.setDueDate(updatedTodo.getDueDate());

    this.todoRepository.save(existingTodo);
  }

  public Todo getOwnedTodo(long id, String ownerEmail) {
    Todo todo = this.todoRepository
      .findById(id)
      .orElseThrow(NotFoundException::new);

    if (!todo.getOwner().getEmail().equals(ownerEmail)) {
      throw new ForbiddenException();
    }

    return todo;
  }

  public void delete(long id, String ownerEmail) {
    Todo toBeDeletedTodo = getOwnedTodo(id, ownerEmail);
    this.todoRepository.delete(toBeDeletedTodo);
  }
}
