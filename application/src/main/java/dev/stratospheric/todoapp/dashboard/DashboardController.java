package dev.stratospheric.todoapp.dashboard;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }


  @GetMapping
  public String getDashboard(Model model, @AuthenticationPrincipal OidcUser user) {
    model.addAttribute("collaborators", List.of());

    if (user != null) {
      model.addAttribute("collaborators", dashboardService.getAvailableCollaborators(user.getEmail()));
      model.addAttribute("todos", dashboardService.getAllOwnedAndSharedTodos(user.getEmail()));
    }

    return "dashboard";
  }
}
