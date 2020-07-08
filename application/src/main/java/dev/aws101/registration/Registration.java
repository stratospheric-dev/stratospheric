package dev.aws101.registration;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class Registration {

  @NotEmpty
  private String username;

  @Email
  private String email;

  @Pattern(message = "Password does not match password policy.",
    regexp = "(?=.{12,})(?=.*?[^\\w\\s])(?=.*?[0-9])(?=.*?[A-Z]).*?[a-z].*")
  private String password;

  @NotEmpty
  private String invitationCode;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  @Override
  public String toString() {
    return "Registration{" +
      "username='" + username + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", invitationCode='" + invitationCode + '\'' +
      '}';
  }
}
