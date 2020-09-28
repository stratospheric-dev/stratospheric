package dev.aws101.todo;

import dev.aws101.collaboration.TodoCollaborationRequest;
import dev.aws101.person.Person;
import dev.aws101.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl implements TodoService {

  private final TodoRepository todoRepository;
  private final PersonRepository personRepository;
  private final QueueMessagingTemplate queueMessagingTemplate;
  private final String todoSharingQueueName;
  private final NotificationMessagingTemplate notificationMessagingTemplate;
  private final String todoTodoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoServiceImpl.class.getName());

  public TodoServiceImpl(
    TodoRepository todoRepository,
    PersonRepository personRepository,
    QueueMessagingTemplate queueMessagingTemplate,
    @Value("${custom.sharing-queue}") String todoSharingQueueName,
    NotificationMessagingTemplate notificationMessagingTemplate,
    @Value("${custom.updates-topic}") String todoTodoUpdatesTopic) {
    this.todoRepository = todoRepository;
    this.personRepository = personRepository;
    this.queueMessagingTemplate = queueMessagingTemplate;
    this.todoSharingQueueName = todoSharingQueueName;
    this.notificationMessagingTemplate = notificationMessagingTemplate;
    this.todoTodoUpdatesTopic = todoTodoUpdatesTopic;
  }

  @Override
  public Todo save(Todo todo) {
    if (todo.getOwner() == null) {
      final String username = SecurityContextHolder.getContext().getAuthentication().getName();
      Person person = personRepository
        .findByName(username)
        .orElse(null);
      if (person == null) {

        Person newUser = new Person();
        newUser.setName(username);
        newUser.setEmail(((DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("email"));

        person = personRepository.save(newUser);
      }
      todo.setOwner(person);
    }

    return todoRepository.save(todo);
  }

  @Override
  public String shareWithCollaborator(long todoId, long collaboratorId) {
    Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("Invalid todo id:" + todoId));
    Person collaborator = personRepository.findById(collaboratorId).orElseThrow(() -> new IllegalArgumentException("Invalid collaborator id:" + collaboratorId));

    LOG.info("About to share todo with id " + todoId + " with collaborator " + collaboratorId);

    TodoCollaborationRequest collaborationRequest = new TodoCollaborationRequest();
    collaborationRequest.setCollaboratorEmail(collaborator.getEmail());
    collaborationRequest.setCollaboratorName(collaborator.getName());
    collaborationRequest.setCollaboratorId(collaboratorId);

    collaborationRequest.setTodoTitle(todo.getTitle());
    collaborationRequest.setTodoDescription(todo.getDescription());
    collaborationRequest.setTodoId(todoId);
    collaborationRequest.setTodoPriority(todo.getPriority());

    queueMessagingTemplate.convertAndSend(todoSharingQueueName, collaborationRequest);

    return collaborator.getName();
  }

  @Override
  public String confirmCollaboration(long todoId, long collaboratorId) {
    notificationMessagingTemplate.sendNotification(todoTodoUpdatesTopic, "message", "subject");

    return "Collaboration confirmed.";
  }
}
