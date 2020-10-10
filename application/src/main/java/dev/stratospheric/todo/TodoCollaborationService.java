package dev.stratospheric.todo;

public interface TodoCollaborationService {

  String shareWithCollaborator(long todoId, long collaboratorId);

  String confirmCollaboration(long todoId, long collaboratorId, String token);
}
