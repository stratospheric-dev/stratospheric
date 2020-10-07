package dev.aws101.todo;

import dev.aws101.collaboration.TodoCollaborationRequest;
import dev.aws101.collaboration.TodoCollaborationRequestRepository;
import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TodoCollaborationServiceImpl implements TodoCollaborationService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;
  private final TodoCollaborationRequestRepository todoCollaborationRequestRepository;
  private final QueueMessagingTemplate queueMessagingTemplate;
  private final String todoSharingQueueName;
  private final NotificationMessagingTemplate notificationMessagingTemplate;
  private final String todoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoCollaborationServiceImpl.class.getName());

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";
  private static final String INVALID_PERSON_ID = "Invalid person ID: ";
  private static final String INVALID_TODO_OR_COLLABORATOR = "Invalid todo or collaborator.";

  public TodoCollaborationServiceImpl(
    TodoRepository todoRepository,
    PersonRepository personRepository,
    TodoCollaborationRequestRepository todoCollaborationRequestRepository, QueueMessagingTemplate queueMessagingTemplate,
    @Value("${custom.sharing-queue}") String todoSharingQueueName,
    NotificationMessagingTemplate notificationMessagingTemplate,
    @Value("${custom.updates-topic}") String todoUpdatesTopic) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
    this.todoCollaborationRequestRepository = todoCollaborationRequestRepository;
    this.queueMessagingTemplate = queueMessagingTemplate;
    this.todoSharingQueueName = todoSharingQueueName;
    this.notificationMessagingTemplate = notificationMessagingTemplate;
    this.todoUpdatesTopic = todoUpdatesTopic;
  }

  @Override
  public String shareWithCollaborator(long todoId, long collaboratorId) {
    Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + todoId));
    Person collaborator = personRepository.findById(collaboratorId).orElseThrow(() -> new IllegalArgumentException("Invalid collaborator id:" + collaboratorId));

    LOG.info("About to share todo with id {} with collaborator {}", todoId, collaboratorId);

    TodoCollaborationRequest collaborationRequest = new TodoCollaborationRequest();
    String token = new DigestUtils("SHA3-256")
      .digestAsHex(
        todo.toString() +
          collaborator.toString() +
          LocalDateTime.now().toString()
      );
    collaborationRequest.setToken(token);
    collaborationRequest.setCollaborator(collaborator);
    collaborationRequest.setTodo(todo);
    todo.getCollaborationRequests().add(collaborationRequest);
    todoRepository.save(todo);

    queueMessagingTemplate.convertAndSend(todoSharingQueueName, collaborationRequest);

    return collaborator.getName();
  }

  @Override
  public String confirmCollaboration(long todoId, long collaboratorId, String token) {
    Todo todo = todoRepository
      .findById(todoId)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));
    Person collaborator = personRepository
      .findById(collaboratorId)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_PERSON_ID + collaboratorId));
    TodoCollaborationRequest todoCollaborationRequest = todoCollaborationRequestRepository
      .findByTodoAndCollaborator(todo, collaborator)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_OR_COLLABORATOR));

    if (todoCollaborationRequest.getToken().equals(token)) {
      String subject = "Collaboration confirmed.";
      String message = "User "
        + todoCollaborationRequest.getCollaborator().getName()
        + " has accepted your collaboration request for todo #"
        + todoCollaborationRequest.getTodo().getId()
        + ".";
      notificationMessagingTemplate.sendNotification(
        todoUpdatesTopic,
        message,
        subject
      );

      todoCollaborationRequestRepository.delete(todoCollaborationRequest);

      return subject;
    }

    return "Collaboration request invalid.";
  }
}
