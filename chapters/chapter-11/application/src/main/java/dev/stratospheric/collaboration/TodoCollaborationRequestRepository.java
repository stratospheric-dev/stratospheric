package dev.stratospheric.collaboration;

import dev.stratospheric.person.Person;
import dev.stratospheric.todo.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {

  Optional<TodoCollaborationRequest> findByTodoAndCollaborator(Todo todo, Person person);
}
