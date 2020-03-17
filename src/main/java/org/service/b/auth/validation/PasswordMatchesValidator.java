package org.service.b.auth.validation;

import org.service.b.auth.validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    NewPasswordMatch newPasswordMatch = (NewPasswordMatch) obj;
    return newPasswordMatch.getPassword().equals(newPasswordMatch.getPasswordConfirmation());
  }

  @Override
  public void initialize(PasswordMatches constraintAnnotation) {

  }
}
