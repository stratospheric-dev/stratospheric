package dev.stratospheric.todo;

import dev.stratospheric.collaboration.TodoCollaborationRequest;
import dev.stratospheric.collaboration.TodoCollaborationNotification;
import dev.stratospheric.collaboration.TodoCollaborationRequestRepository;
import dev.stratospheric.person.Person;
import dev.stratospheric.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
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
  private final NotificationMessagingTemplate notificationMessagingTemplate;

  private final String todoSharingQueueName;
  private final String todoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoCollaborationService.class.getName());

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";
  private static final String INVALID_PERSON_ID = "Invalid person ID: ";
  private static final String INVALID_TODO_OR_COLLABORATOR = "Invalid todo or collaborator.";

  public TodoCollaborationService(
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

  public String shareWithCollaborator(Long todoId, Long collaboratorId) {
    Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + todoId));
    Person collaborator = personRepository.findById(collaboratorId).orElseThrow(() -> new IllegalArgumentException("Invalid collaborator id:" + collaboratorId));

    LOG.info("About to share todo with id {} with collaborator {}", todoId, collaboratorId);

    TodoCollaborationRequest collaboration = new TodoCollaborationRequest();
    String token = UUID.randomUUID().toString();
    collaboration.setToken(token);
    collaboration.setCollaborator(collaborator);
    collaboration.setTodo(todo);
    todo.getCollaborationRequests().add(collaboration);

    todoRepository.save(todo);

    queueMessagingTemplate.convertAndSend(todoSharingQueueName, new TodoCollaborationNotification(collaboration));

    return collaborator.getName();
  }

  public String confirmCollaboration(Long todoId, Long collaboratorId, String token) {
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
