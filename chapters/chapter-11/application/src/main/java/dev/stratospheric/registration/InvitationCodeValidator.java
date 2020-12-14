package dev.stratospheric.registration;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class InvitationCodeValidator implements ConstraintValidator<ValidInvitationCode, Registration> {

  private final Set<String> validInvitationCodes;

  public InvitationCodeValidator(@Value("${custom.invitation-codes:none}") Set<String> validInvitationCodes) {
    this.validInvitationCodes = validInvitationCodes;
  }

  @Override
  public void initialize(ValidInvitationCode constraintAnnotation) {
    // intentionally left empty
  }

  @Override
  public boolean isValid(Registration value, ConstraintValidatorContext context) {
    return validInvitationCodes.contains(value.getInvitationCode());
  }
}
