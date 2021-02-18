package dev.stratospheric.todoapp.collaboration;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.todo.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {
  TodoCollaborationRequest findByTodoAndCollaborator(Todo todo, Person person);
  TodoCollaborationRequest findByTodoIdAndCollaboratorId(Long todoId, Long collaboratorId);
}
