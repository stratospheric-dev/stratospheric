package dev.stratospheric.todoapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class LoggingContextInterceptor implements HandlerInterceptor {

  private final Logger logger = LoggerFactory.getLogger(LoggingContextInterceptor.class);

  @Override
  public boolean preHandle(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Object handler) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = getUserIdFromPrincipal(authentication.getPrincipal());
    logger.info("Intercepted request. UserId: {}", userId);
    MDC.put("userId", userId);
    return true;
  }

  private String getUserIdFromPrincipal(Object principal) {
    if (principal instanceof String) {
      // anonymous users will have a String principal with value "anonymousUser"
      return principal.toString();
    }

    if (principal instanceof OidcUser) {
      try {
        return ((OidcUser) principal).getUserInfo().getPreferredUsername();
      } catch (Exception e) {
        logger.warn("could not extract userId from Principal", e);
      }
    }
    return "unknown";
  }

  @Override
  public void afterCompletion(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Object handler,
    final Exception ex) {
    MDC.clear();
  }
}
