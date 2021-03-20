package dev.stratospheric.collaboration;

import dev.stratospheric.person.Person;
import dev.stratospheric.todo.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {
  TodoCollaborationRequest findByTodoAndCollaborator(Todo todo, Person person);
  TodoCollaborationRequest findByTodoIdAndCollaboratorId(Long todoId, Long collaboratorId);
}
