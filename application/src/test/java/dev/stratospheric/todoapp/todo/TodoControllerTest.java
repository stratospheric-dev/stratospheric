package dev.stratospheric.todoapp.todo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import dev.stratospheric.todoapp.AbstractDevIntegrationTest;
import dev.stratospheric.todoapp.person.PersonRepository;
import dev.stratospheric.todoapp.util.SecurityContextFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest extends AbstractDevIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TodoController todoController;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Test
  void contextLoads() {
    assertNotNull(mockMvc);
  }

  @Test
  void shouldAllowCrudOperationOnTodo() throws Exception {
    SecurityContextFactory.createSecurityContext("info@stratospheric.dev");

    OidcUser user = new DefaultOidcUser(
      null,
      new OidcIdToken(
        "emailAddress",
        Instant.now(),
        Instant.MAX,
        Map.of(
          "email", "emailAddress",
          "sub", "emailAddress",
          "name", "duke"
        )
      )
    );

    this.mockMvc
      .perform(post("/todo")
        .with(authentication(new TestingAuthenticationToken(user, null)))
          .param("title", "Duke")
          .param("description", "" )
          .param("dueDate", LocalDate.now().plusDays(42).toString())
        )
        .andExpect(status().isOk());

    assertThat(todoRepository.findAll())
      .hasSize(1);

    assertThat(personRepository.findAll())
      .hasSize(1);
  }
}
