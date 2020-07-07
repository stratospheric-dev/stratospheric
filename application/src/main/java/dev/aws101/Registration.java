package dev.aws101;

public class Registration {

  private String email;
  private String password;
  private String invitationCode;

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
      "email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", invitationCode='" + invitationCode + '\'' +
      '}';
  }
}
