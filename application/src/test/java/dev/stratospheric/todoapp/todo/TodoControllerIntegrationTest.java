package dev.stratospheric.todoapp.todo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTest extends AbstractDevIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private TodoRepository todoRepository;

  private OidcUser user = new DefaultOidcUser(
    null,
    new OidcIdToken(
      "emailAddress",
      Instant.now(),
      Instant.MAX,
      Map.of(
        "email", "duke@stratospheric.dev",
        "sub", "stratospheric",
        "name", "duke"
      )
    )
  );

  @Test
  void shouldAllowCrudOperationOnTodo() throws Exception {
    shouldCreateTodo();
    shouldShowCreatedTodo();
    shouldUpdateTodo();
    shouldDeleteTodo();
    shouldNotFindDeletedTodo();
  }

  @Test
  void shouldAllowCollaboratingOnSharedTodo() throws Exception {

    OidcUser collaborator = new DefaultOidcUser(
      null,
      new OidcIdToken(
        "emailAddress",
        Instant.now(),
        Instant.MAX,
        Map.of(
          "email", "collaborator@stratospheric.dev",
          "sub", "stratospheric",
          "name", "collaborator"
        )
      )
    );

    Long sharedTodoId = givenSharedTodo();

    shouldAllowViewingSharedTodo(sharedTodoId, collaborator);
    shouldAllowUpdatingSharedTodo(sharedTodoId, collaborator);
    shouldNotAllowDeletingSharedTodo(sharedTodoId, collaborator);
  }

  private void shouldNotAllowDeletingSharedTodo(Long sharedTodoId, OidcUser collaborator) throws Exception {
    this.mockMvc
      .perform(get("/todo/delete/" + sharedTodoId)
        .with(oidcLogin().oidcUser(collaborator)))
      .andExpect(status().isForbidden());
  }

  private void shouldAllowViewingSharedTodo(Long sharedTodoId, OidcUser collaborator) throws Exception {
    this.mockMvc
      .perform(get("/todo/show/"+ sharedTodoId)
        .with(oidcLogin().oidcUser(collaborator)))
      .andExpect(status().isOk());
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
    sharedTodo.setCollaborators(Arrays.asList(todoCollaborator));

    Todo savedTodo = todoRepository.save(sharedTodo);
    return savedTodo.getId();
  }

  private void shouldNotFindDeletedTodo() throws Exception {
    this.mockMvc
      .perform(get("/todo/show/1")
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().isNotFound());
  }

  private void shouldDeleteTodo() throws Exception {
    this.mockMvc
      .perform(get("/todo/delete/1")
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }

  private void shouldAllowUpdatingSharedTodo(Long sharedTodoId, OidcUser collaborator) throws Exception {
    this.mockMvc
      .perform(post("/todo/update/" + sharedTodoId)
        .with(oidcLogin().oidcUser(collaborator))
        .with(csrf())
        .param("title", "Updated Title")
        .param("description", "Updated Description")
        .param("dueDate", LocalDate.now().plusDays(30).toString()))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }

  private void shouldUpdateTodo() throws Exception {
    this.mockMvc
      .perform(post("/todo/update/1")
        .with(oidcLogin().oidcUser(user))
        .with(csrf())
        .param("title", "Updated Title")
        .param("description", "Updated Description")
        .param("dueDate", LocalDate.now().plusDays(30).toString()))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }

  private void shouldShowCreatedTodo() throws Exception {
    this.mockMvc
      .perform(get("/todo/show/1")
        .with(oidcLogin().oidcUser(user)))
      .andExpect(status().isOk());
  }

  private void shouldCreateTodo() throws Exception {
    this.mockMvc
      .perform(post("/todo")
        .with(oidcLogin().oidcUser(user))
        .with(csrf())
        .param("title", "Duke")
        .param("description", "Duke's gonna test it")
        .param("dueDate", LocalDate.now().plusDays(42).toString())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:/dashboard"));
  }
}
