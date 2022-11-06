package dev.stratospheric.todoapp.collaboration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.person.PersonRepository;
import dev.stratospheric.todoapp.todo.Todo;
import dev.stratospheric.todoapp.todo.TodoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

@Service
@Transactional
public class TodoCollaborationService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;
  private final TodoCollaborationRequestRepository todoCollaborationRequestRepository;

  private final SqsClient sqsClient;
  private final String todoSharingQueueName;

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final ObjectMapper objectMapper;

  private static final Logger LOG = LoggerFactory.getLogger(TodoCollaborationService.class.getName());

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";
  private static final String INVALID_PERSON_ID = "Invalid person ID: ";
  private static final String INVALID_PERSON_EMAIL = "Invalid person Email: ";

  public TodoCollaborationService(
    @Value("${custom.sharing-queue}") String todoSharingQueueName,
    TodoRepository todoRepository,
    PersonRepository personRepository,
    TodoCollaborationRequestRepository todoCollaborationRequestRepository,
    SqsClient sqsClient,
    SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
    this.todoCollaborationRequestRepository = todoCollaborationRequestRepository;
    this.sqsClient = sqsClient;
    this.todoSharingQueueName = todoSharingQueueName;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.objectMapper = objectMapper;
  }

  public String shareWithCollaborator(String todoOwnerEmail, Long todoId, Long collaboratorId) throws JsonProcessingException {

    Todo todo = todoRepository
      .findByIdAndOwnerEmail(todoId, todoOwnerEmail)
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

    sqsClient.sendMessage(
      SendMessageRequest
        .builder()
        .queueUrl(todoSharingQueueName)
        .messageBody(
          objectMapper.writeValueAsString(
            new TodoCollaborationNotification(collaboration)
          )
        )
        .build()
    );

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

    LOG.info("Collaboration request: {}", collaborationRequest);

    if (collaborationRequest == null || !collaborationRequest.getToken().equals(token)) {
      return false;
    }

    LOG.info("Original collaboration token: {}", collaborationRequest.getToken());
    LOG.info("Request token: {}", token);

    Todo todo = todoRepository
      .findById(todoId)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));

    todo.addCollaborator(collaborator);

    todoCollaborationRequestRepository.delete(collaborationRequest);

    String name = collaborationRequest.getCollaborator().getName();
    String subject = "Collaboration confirmed.";
    String message = "User "
      + name
      + " has accepted your collaboration request for todo #"
      + collaborationRequest.getTodo().getId()
      + ".";
    String ownerEmail = collaborationRequest.getTodo().getOwner().getEmail();

    simpMessagingTemplate.convertAndSend("/topic/todoUpdates/" + ownerEmail, subject + " " + message);

    LOG.info("Successfully informed owner about accepted request.");

    return true;
  }
}
