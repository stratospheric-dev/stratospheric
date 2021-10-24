package dev.stratospheric.collaboration;

import dev.stratospheric.person.Person;
import dev.stratospheric.person.PersonRepository;
import dev.stratospheric.todo.Todo;
import dev.stratospheric.todo.TodoRepository;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class TodoCollaborationService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;
  private final TodoCollaborationRequestRepository todoCollaborationRequestRepository;

  private final QueueMessagingTemplate queueMessagingTemplate;
  private final String todoSharingQueueName;

  private static final Logger LOG = LoggerFactory.getLogger(TodoCollaborationService.class.getName());

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";
  private static final String INVALID_PERSON_ID = "Invalid person ID: ";
  private static final String INVALID_PERSON_EMAIL = "Invalid person Email: ";

  public TodoCollaborationService(
    @Value("${custom.sharing-queue}") String todoSharingQueueName,
    TodoRepository todoRepository,
    PersonRepository personRepository,
    TodoCollaborationRequestRepository todoCollaborationRequestRepository,
    QueueMessagingTemplate queueMessagingTemplate) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
    this.todoCollaborationRequestRepository = todoCollaborationRequestRepository;
    this.queueMessagingTemplate = queueMessagingTemplate;
    this.todoSharingQueueName = todoSharingQueueName;
  }

  public String shareWithCollaborator(String email, Long todoId, Long collaboratorId) {

    Todo todo = todoRepository
      .findByIdAndOwnerEmail(todoId, email)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));

    Person collaborator = personRepository
      .findById(collaboratorId)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_PERSON_ID + collaboratorId));

    if (todoCollaborationRequestRepository.findByTodoAndCollaborator(todo, collaborator) != null) {
      LOG.info("Collaboration request for todo {} with collaborator {} already exists", todoId, collaboratorId);
      return collaborator.getName();
    }

    LOG.info("About to share todo with id {} with collaborator {}", todoId, collaboratorId);

    TodoCollaborationRequest collaboration = new TodoCollaborationRequest();
    String token = UUID.randomUUID().toString();
    collaboration.setToken(token);
    collaboration.setCollaborator(collaborator);
    collaboration.setTodo(todo);
    todo.getCollaborationRequests().add(collaboration);

    todoCollaborationRequestRepository.save(collaboration);

    queueMessagingTemplate.convertAndSend(todoSharingQueueName, new TodoCollaborationNotification(collaboration));

    return collaborator.getName();
  }

  public boolean confirmCollaboration(String authenticatedUserEmail, Long todoId, Long collaboratorId, String token) {

    Person collaborator = personRepository
      .findByEmail(authenticatedUserEmail)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_PERSON_EMAIL + authenticatedUserEmail));

    if (!collaborator.getId().equals(collaboratorId)) {
      return false;
    }

    TodoCollaborationRequest collaborationRequest = todoCollaborationRequestRepository
      .findByTodoIdAndCollaboratorId(todoId, collaboratorId);

    if (collaborationRequest == null || !collaborationRequest.getToken().equals(token)) {
      return false;
    }

    Todo todo = todoRepository
      .findById(todoId)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));

    todo.addCollaborator(collaborator);

    todoCollaborationRequestRepository.delete(collaborationRequest);

    return true;
  }
}
