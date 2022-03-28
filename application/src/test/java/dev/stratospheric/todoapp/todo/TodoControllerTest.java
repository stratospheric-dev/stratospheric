package dev.stratospheric.todoapp.todo;

import dev.stratospheric.todoapp.person.Person;
import dev.stratospheric.todoapp.util.SecurityContextFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired
  private TodoController todoController;

  @MockBean
  private TodoService todoService;

  @MockBean
  private LogoutSuccessHandler logoutSuccessHandler;

  @MockBean
  private ClientRegistrationRepository clientRegistrationRepository;

  private Todo todo;

  @BeforeEach
  void setUp() {
    Person owner = new Person();
    owner.setEmail("info@stratospheric.dev");

    todo = new Todo();
    todo.setId(1L);
    todo.setOwner(owner);

    given(todoService.findById(1L)).willReturn(Optional.of(todo));
  }

  @Test
  void withMatchingCredentials() {
    SecurityContextFactory.createSecurityContext("info@stratospheric.dev");

    OidcUser user = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Model model = new ExtendedModelMap();

    assertEquals("todo/show", todoController.showView(user, 1L, model));
    assertEquals("todo/edit", todoController.editView(user, 1L, model));

    RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
    assertEquals("redirect:/dashboard",
      todoController.update(
        user,
        1L,
        todo,
        new MapBindingResult(model.asMap(), "todo"),
        model, redirectAttributes
      )
    );
    assertEquals("redirect:/dashboard",
      todoController.delete(user,
        1L,
        redirectAttributes
      )
    );
  }

  @Test
  void withoutMatchingCredentials() {
    SecurityContextFactory.createSecurityContext("somebody-else@stratospheric.dev");

    OidcUser user = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Model model = new ExtendedModelMap();

    assertThrows(ForbiddenException.class, () ->
      todoController.showView(user, 1L, model)
    );
    assertThrows(ForbiddenException.class, () ->
      todoController.editView(user, 1L, model)
    );

    BindingResult bindingResult = new MapBindingResult(model.asMap(), "todo");
    RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
    assertThrows(ForbiddenException.class, () ->
      todoController.update(
        user,
        1L,
        todo,
        bindingResult,
        model,
        redirectAttributes
      )
    );
    assertThrows(ForbiddenException.class, () -> {
      todoController.delete(user,
        1L,
        redirectAttributes
      );
    });
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }
}
