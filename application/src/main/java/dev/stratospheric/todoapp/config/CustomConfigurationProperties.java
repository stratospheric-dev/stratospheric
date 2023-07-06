package dev.stratospheric.todoapp.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@ConfigurationProperties(prefix = "custom")
@Validated
class CustomConfigurationProperties {

  @NotEmpty
  private Set<String> invitationCodes;

  @NotEmpty
  private String sharingQueue;

  @NotEmpty
  private String externalUrl;

  private boolean autoConfirmCollaborations;

  @NotEmpty
  private String environment;

  @NotEmpty
  private String confirmEmailFromAddress;

  private boolean webSocketRelayUseSsl;

  private boolean useCognitoAsIdentityProvider;

  private boolean provideTestTodoController;

  public Set<String> getInvitationCodes() {
    return invitationCodes;
  }

  public void setInvitationCodes(Set<String> invitationCodes) {
    this.invitationCodes = invitationCodes;
  }

  public String getSharingQueue() {
    return sharingQueue;
  }

  public void setSharingQueue(String sharingQueue) {
    this.sharingQueue = sharingQueue;
  }

  public String getExternalUrl() {
    return externalUrl;
  }

  public void setExternalUrl(String externalUrl) {
    this.externalUrl = externalUrl;
  }

  public Boolean getAutoConfirmCollaborations() {
    return autoConfirmCollaborations;
  }

  public void setAutoConfirmCollaborations(Boolean autoConfirmCollaborations) {
    this.autoConfirmCollaborations = autoConfirmCollaborations;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getConfirmEmailFromAddress() {
    return confirmEmailFromAddress;
  }

  public void setConfirmEmailFromAddress(String confirmEmailFromAddress) {
    this.confirmEmailFromAddress = confirmEmailFromAddress;
  }

  public Boolean getWebSocketRelayUseSsl() {
    return webSocketRelayUseSsl;
  }

  public void setWebSocketRelayUseSsl(Boolean webSocketRelayUseSsl) {
    this.webSocketRelayUseSsl = webSocketRelayUseSsl;
  }

  public Boolean getUseCognitoAsIdentityProvider() {
    return useCognitoAsIdentityProvider;
  }

  public void setUseCognitoAsIdentityProvider(Boolean useCognitoAsIdentityProvider) {
    this.useCognitoAsIdentityProvider = useCognitoAsIdentityProvider;
  }

  public Boolean getProvideTestTodoController() {
    return provideTestTodoController;
  }

  public void setProvideTestTodoController(Boolean provideTestTodoController) {
    this.provideTestTodoController = provideTestTodoController;
  }
}
