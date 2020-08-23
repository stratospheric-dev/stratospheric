package dev.aws101.todo;

public interface TodoService {

  Todo save(Todo todo);

  String shareWithCollaborator(long todoId, long collaboratorId);
}
