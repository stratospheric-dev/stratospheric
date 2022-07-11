package dev.stratospheric.config;

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

  @NotNull
  private Boolean autoConfirmCollaborations;

  @NotNull
  private Boolean useCognitoAsIdentityProvider;

  public String getSharingQueue() {
    return sharingQueue;
  }

  public void setSharingQueue(String sharingQueue) {
    this.sharingQueue = sharingQueue;
  }

  public Boolean getAutoConfirmCollaborations() {
    return autoConfirmCollaborations;
  }

  public void setAutoConfirmCollaborations(Boolean autoConfirmCollaborations) {
    this.autoConfirmCollaborations = autoConfirmCollaborations;
  }

  public Boolean getUseCognitoAsIdentityProvider() {
    return useCognitoAsIdentityProvider;
  }

  public void setUseCognitoAsIdentityProvider(Boolean useCognitoAsIdentityProvider) {
    this.useCognitoAsIdentityProvider = useCognitoAsIdentityProvider;
  }

  public Set<String> getInvitationCodes() {
    return invitationCodes;
  }

  public void setInvitationCodes(Set<String> invitationCodes) {
    this.invitationCodes = invitationCodes;
  }

}
