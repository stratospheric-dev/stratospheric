package dev.stratospheric.todoapp.todo;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.util.SecurityContextFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

@WebMvcTest(TodoController.class)
class TodoControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TodoService todoService;

  @MockBean
  private LogoutSuccessHandler logoutSuccessHandler;

  @MockBean
  private ClientRegistrationRepository clientRegistrationRepository;

  @BeforeEach
  void setUp() {
    Person owner = new Person();
    owner.setEmail("info@stratospheric.dev");

    Todo todo = new Todo();
    todo.setId(1L);
    todo.setOwner(owner);

    given(todoService.findById(1L)).willReturn(Optional.of(todo));
  }

  @Test
  void withMatchingCredentials() throws Exception {
    OidcUser user = createOidcUser("info@stratospheric.dev");

    performGetRequest("/todo/show/1", user)
      .andExpect(MockMvcResultMatchers.status().isOk());

    performGetRequest("/todo/edit/1", user)
      .andExpect(MockMvcResultMatchers.status().isOk());

    performGetRequest("/todo/delete/1", user)
      .andExpect(MockMvcResultMatchers.status().isFound());
  }

  @Test
  void withoutMatchingCredentials() throws Exception {
    OidcUser user = createOidcUser("somebody-else@stratospheric.dev");

    performGetRequest("/todo/show/1", user)
      .andExpect(MockMvcResultMatchers.status().isForbidden());

    performGetRequest("/todo/edit/1", user)
      .andExpect(MockMvcResultMatchers.status().isForbidden());

    performGetRequest("/todo/delete/1", user)
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @NotNull
  private ResultActions performGetRequest(String urlTemplate, OidcUser user) throws Exception {
    return mockMvc.perform(
      MockMvcRequestBuilders
        .get(urlTemplate)
        .with(
          oidcLogin()
            .oidcUser(user)
        )
    );
  }

  private OidcUser createOidcUser(String emailAddress) {
    SecurityContextFactory.createSecurityContext(emailAddress);

    return (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
