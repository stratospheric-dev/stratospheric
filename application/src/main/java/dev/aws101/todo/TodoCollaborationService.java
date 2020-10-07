package dev.aws101.todo;

public interface TodoCollaborationService {

  String shareWithCollaborator(long todoId, long collaboratorId);

  String confirmCollaboration(long todoId, long collaboratorId, String token);
}
