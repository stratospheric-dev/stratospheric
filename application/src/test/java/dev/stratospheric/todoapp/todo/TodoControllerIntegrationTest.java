package dev.stratospheric.todoapp.todo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import dev.stratospheric.todoapp.AbstractDevIntegrationTest;
import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTest extends AbstractDevIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Test
  void shouldAllowCrudOperationOnTodo() throws Exception {

    OidcUser todoOwner = createOidcUser("duke@stratospheric.dev", "duke");

    Long createdTodoId = shouldCreateTodo(todoOwner);
    shouldAllowViewingTodo(createdTodoId, todoOwner);
    shouldAllowUpdatingTodo(createdTodoId, todoOwner);
    shouldAllowDeletingTodo(createdTodoId, todoOwner);
    shouldNotFindTodo(createdTodoId, todoOwner);
  }

  @Test
  void shouldAllowCollaboratingOnSharedTodo() throws Exception {

    OidcUser collaborator = createOidcUser("collaborator@stratospheric.dev", "collaborator");

    Long sharedTodoId = givenSharedTodo();

    shouldAllowViewingTodo(sharedTodoId, collaborator);
    shouldAllowUpdatingTodo(sharedTodoId, collaborator);
    shouldNotAllowDeletingSharedTodo(sharedTodoId, collaborator);
  }

  private void shouldNotAllowDeletingSharedTodo(Long sharedTodoId, OidcUser collaborator) throws Exception {
    this.mockMvc
      .perform(get("/todo/delete/" + sharedTodoId)
        .with(oidcLogin().oidcUser(collaborator)))
      .andExpect(status().isForbidden());
  }

  private Long givenSharedTodo() {
    Person todoOwner = new Person();
    todoOwner.setName("duke");
    todoOwner.setEmail("duke@stratospheric.dev");

    Person todoCollaborator = new Person();
    todoCollaborator.setName("collaborator");
    todoCollaborator.setEmail("collaborator@stratospheric.dev");

    personRepository.saveAll(List.of(todoCollaborator, todoOwner));

    Todo sharedTodo = new Todo();
    sharedTodo.setDueDate(LocalDate.now().plusDays(42));
    sharedTodo.setTitle("Wash dishes");
    sharedTodo.setDescription("Soap ftw!");
    sharedTodo.setStatus(Status.OPEN);
    sharedTodo.setPriority(Priority.DEFAULT);
    sharedTodo.setOwner(todoOwner);
    sharedTodo.getCollaborators().add(todoCollaborator);

    Todo savedTodo = todoRepository.save(sharedTodo);
    return savedTodo.getId();
  }

  private void shouldNotFindTodo(Long todoId, OidcUser user) throws Exception {
    this.mockMvc
      .perform(get("/todo/show/" + todoId)
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().isNotFound());
  }

  private void shouldAllowDeletingTodo(Long todoId, OidcUser user) throws Exception {
    this.mockMvc
      .perform(get("/todo/delete/" + todoId)
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }

  private void shouldAllowUpdatingTodo(Long todoId, OidcUser user) throws Exception {
    this.mockMvc
      .perform(post("/todo/update/" + todoId)
        .with(oidcLogin().oidcUser(user))
        .with(csrf())
        .param("title", "Updated Title")
        .param("description", "Updated Description")
        .param("dueDate", LocalDate.now().plusDays(30).toString()))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }

  private void shouldAllowViewingTodo(Long todoId, OidcUser user) throws Exception {
    this.mockMvc
      .perform(get("/todo/show/" + todoId)
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().isOk());
  }

  private Long shouldCreateTodo(OidcUser user) throws Exception {
    MvcResult result = this.mockMvc
      .perform(post("/todo")
        .with(oidcLogin().oidcUser(user))
        .with(csrf())
        .param("title", "Duke")
        .param("description", "Duke's gonna test it")
        .param("dueDate", LocalDate.now().plusDays(42).toString())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"))
      .andReturn();

    return (Long) result.getFlashMap().get("todoId");
  }

  private DefaultOidcUser createOidcUser(String email, String username) {
    return new DefaultOidcUser(
      null,
      new OidcIdToken(
        "some-id",
        Instant.now(),
        Instant.MAX,
        Map.of(
          "email", email,
          "sub", "stratospheric",
          "name", username
        )
      )
    );
  }
}
