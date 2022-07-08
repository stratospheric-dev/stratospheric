package dev.stratospheric.todoapp.todo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import dev.stratospheric.todoapp.AbstractDevIntegrationTest;
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

  private OidcUser user = new DefaultOidcUser(
    null,
    new OidcIdToken(
      "emailAddress",
      Instant.now(),
      Instant.MAX,
      Map.of(
        "email", "info@stratospheric.dev",
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
