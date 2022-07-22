package dev.stratospheric.todoapp.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "custom")
@Validated
class CustomConfigurationProperties {

  @NotEmpty
  private Set<String> invitationCodes;

  @NotEmpty
  private String sharingQueue;

  @NotEmpty
  private String externalUrl;

  @NotNull
  private Boolean autoConfirmCollaborations;

  @NotEmpty
  private String environment;

  @NotEmpty
  private String confirmEmailFromAddress;

  @NotEmpty
  private String webSocketRelayEndpoint;

  @NotEmpty
  private String webSocketRelayUsername;

  @NotEmpty
  private String webSocketRelayPassword;

  @NotNull
  private Boolean webSocketRelayUseSsl;

  @NotNull
  private Boolean useCognitoAsIdentityProvider;

  @NotNull
  private Boolean provideDynamodbViaAws;

  @NotNull
  private Boolean provideTestTodoController;

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

  public String getWebSocketRelayEndpoint() {
    return webSocketRelayEndpoint;
  }

  public void setWebSocketRelayEndpoint(String webSocketRelayEndpoint) {
    this.webSocketRelayEndpoint = webSocketRelayEndpoint;
  }

  public String getWebSocketRelayUsername() {
    return webSocketRelayUsername;
  }

  public void setWebSocketRelayUsername(String webSocketRelayUsername) {
    this.webSocketRelayUsername = webSocketRelayUsername;
  }

  public String getWebSocketRelayPassword() {
    return webSocketRelayPassword;
  }

  public void setWebSocketRelayPassword(String webSocketRelayPassword) {
    this.webSocketRelayPassword = webSocketRelayPassword;
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

  public Boolean getProvideDynamodbViaAws() {
    return provideDynamodbViaAws;
  }

  public void setProvideDynamodbViaAws(Boolean provideDynamodbViaAws) {
    this.provideDynamodbViaAws = provideDynamodbViaAws;
  }

  public Boolean getProvideTestTodoController() {
    return provideTestTodoController;
  }

  public void setProvideTestTodoController(Boolean provideTestTodoController) {
    this.provideTestTodoController = provideTestTodoController;
  }
}
