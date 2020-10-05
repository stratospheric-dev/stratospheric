package dev.aws101.todo;

import dev.aws101.collaboration.TodoCollaborationRequestRepository;
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
  private final TodoCollaborationRequestRepository todoCollaborationRequestRepository;
  private final QueueMessagingTemplate queueMessagingTemplate;
  private final String todoSharingQueueName;
  private final NotificationMessagingTemplate notificationMessagingTemplate;
  private final String todoUpdatesTopic;

  private static final Logger LOG = LoggerFactory.getLogger(TodoServiceImpl.class.getName());

  private static final String INVALID_TODO_ID = "Invalid todo ID: ";
  private static final String INVALID_PERSON_ID = "Invalid person ID: ";
  private static final String INVALID_TODO_OR_COLLABORATOR = "Invalid todo or collaborator.";

  public TodoServiceImpl(
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
}
