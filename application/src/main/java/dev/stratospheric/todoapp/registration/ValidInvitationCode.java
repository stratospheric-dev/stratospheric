package dev.stratospheric.todoapp.registration;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvitationCodeValidator.class)
public @interface ValidInvitationCode {
  String message() default "Invalid invitation code. Please check it again or contact the person who invited you.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
