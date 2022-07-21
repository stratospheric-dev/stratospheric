package dev.stratospheric.todoapp.todo;

import java.time.LocalDate;

import dev.stratospheric.todoapp.config.WebSecurityConfig;
import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.util.SecurityContextFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(TodoController.class)
@Import(WebSecurityConfig.class)
class TodoControllerTest {

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
    todo.setStatus(Status.OPEN);
    todo.setTitle("Test");
    todo.setDescription("Sample Description");
    todo.setDueDate(LocalDate.now().plusDays(42));
    todo.setId(1L);
    todo.setOwner(owner);

    given(todoService.getOwnedOrSharedTodo(1L, "info@stratospheric.dev")).willReturn(todo);
  }

  @Test
  void shouldAllowAccessForAuthenticatedUser() throws Exception {
    OidcUser user = createOidcUser("info@stratospheric.dev");

    this.mockMvc
      .perform(get("/todo/show/1")
        .with(oidcLogin().oidcUser(user))
      )
      .andExpect(MockMvcResultMatchers.status().isOk());

    this.mockMvc
      .perform(get("/todo/edit/1")
        .with(oidcLogin().oidcUser(user))
      )
      .andExpect(MockMvcResultMatchers.status().isOk());

    this.mockMvc
      .perform(get("/todo/delete/1")
        .with(oidcLogin().oidcUser(user))
      )
      .andExpect(MockMvcResultMatchers.status().isFound());
  }

  @Test
  void shouldRejectAccessForUnknownUser() throws Exception {

    this.mockMvc
      .perform(get("/todo/show/1"))
      .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
      .andExpect(header().string("Location", "http://localhost/login"));

    this.mockMvc
      .perform(get("/todo/edit/1"))
      .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
      .andExpect(header().string("Location", "http://localhost/login"));

    this.mockMvc
      .perform(get("/todo/delete/1"))
      .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
      .andExpect(header().string("Location", "http://localhost/login"));
  }

  private OidcUser createOidcUser(String emailAddress) {
    SecurityContextFactory.createSecurityContext(emailAddress);

    return (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
