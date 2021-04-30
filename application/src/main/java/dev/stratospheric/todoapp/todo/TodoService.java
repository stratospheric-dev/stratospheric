package dev.stratospheric.todoapp.todo;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.person.PersonRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;
  private final MeterRegistry meterRegistry;

  public TodoService(
    TodoRepository todoRepository,
    PersonRepository personRepository,
    MeterRegistry meterRegistry) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
    this.meterRegistry = meterRegistry;
  }

  public Todo save(Todo todo) {
    if (todo.getOwner() == null) {

      OidcUser user = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      String email = user.getEmail();
      Person person = personRepository.findByEmail(email).orElse(null);

      if (person == null) {
        Person newUser = new Person();
        newUser.setName(user.getAttribute("name"));
        newUser.setEmail(email);

        person = personRepository.save(newUser);
      }
      todo.setOwner(person);
      todo.setStatus(Status.OPEN);
    }

    meterRegistry.gauge("stratospheric.todo.created", 1);

    return todoRepository.save(todo);
  }

  public Optional<Todo> findById(Long id) {
    return this.todoRepository.findById(id);
  }

  public void delete(Todo todo) {
    this.todoRepository.delete(todo);
  }
}
