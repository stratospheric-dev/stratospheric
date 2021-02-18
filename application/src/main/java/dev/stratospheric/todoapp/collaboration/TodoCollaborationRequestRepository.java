package dev.stratospheric.todoapp.collaboration;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.todo.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {

  Optional<TodoCollaborationRequest> findByTodoAndCollaborator(Todo todo, Person person);
  Optional<TodoCollaborationRequest> findByTodoIdAndCollaboratorId(Long todoId, Long collaboratorId);
}
