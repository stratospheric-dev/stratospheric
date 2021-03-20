package dev.stratospheric.todoapp.todo;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.person.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;

  public TodoService(
    TodoRepository todoRepository,
    PersonRepository personRepository) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
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

    return todoRepository.save(todo);
  }

  public Optional<Todo> findById(Long id) {
    return this.todoRepository.findById(id);
  }

  public void delete(Todo todo) {
    this.todoRepository.delete(todo);
  }
}
