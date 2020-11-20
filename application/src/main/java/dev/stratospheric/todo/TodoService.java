package dev.stratospheric.todo;

import dev.stratospheric.person.Person;
import dev.stratospheric.person.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

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
        newUser.setName(user.getPreferredUsername());
        newUser.setEmail(email);

        person = personRepository.save(newUser);
      }
      todo.setOwner(person);
      todo.setStatus(Status.OPEN);
    }

    return todoRepository.save(todo);
  }
}
