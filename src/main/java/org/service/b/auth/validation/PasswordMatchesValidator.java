package org.service.b.auth.validation;

import org.service.b.auth.dto.SignUpDto;
import org.service.b.auth.validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    SignUpDto signUpDto = (SignUpDto) obj;
    return signUpDto.getPassword().equals(signUpDto.getPasswordConfirmation());
  }

  @Override
  public void initialize(PasswordMatches constraintAnnotation) {

  }
}
