package dev.aws101.todo;

import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl implements TodoService {

  private final TodoRepository todoRepository;

  private final PersonRepository personRepository;

  public TodoServiceImpl(
    TodoRepository todoRepository,
    PersonRepository personRepository
  ) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
  }

  @Override
  public Todo save(Todo todo) {
    if (todo.getOwner() == null) {
      Person person = personRepository
        .findByName(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
      if (person == null) {
        person = personRepository.findByName("Admin").orElse(null);
      }
      todo.setOwner(person);
    }

    return todoRepository.save(todo);
  }
}
