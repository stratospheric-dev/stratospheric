package dev.aws101.collaboration;

import dev.aws101.person.Person;
import dev.aws101.todo.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {

  Optional<TodoCollaborationRequest> findByTodoAndCollaborator(Todo todo, Person person);
}
