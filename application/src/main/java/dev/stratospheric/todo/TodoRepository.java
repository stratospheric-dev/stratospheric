package dev.stratospheric.todo;

import dev.stratospheric.person.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {

  Iterable<Todo> findAllByOwner(Person person);
}
