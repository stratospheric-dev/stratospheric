package dev.aws101.todo;

import dev.aws101.person.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {

  Iterable<Todo> findAllByOwner(Person person);
}
