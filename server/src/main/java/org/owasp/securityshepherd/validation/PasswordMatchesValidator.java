package org.owasp.securityshepherd.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.owasp.securityshepherd.web.dto.UserDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  @Override
  public void initialize(final PasswordMatches constraintAnnotation) {
    //
  }

  @Override
  public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
    final UserDto user = (UserDto) obj;
    return user.getPassword().equals(user.getMatchingPassword());
  }

}
