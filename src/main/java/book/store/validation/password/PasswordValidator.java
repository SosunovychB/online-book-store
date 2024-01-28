package book.store.validation.password;

import book.store.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator
        implements ConstraintValidator<PasswordMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto userRegistrationRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (userRegistrationRequestDto == null
                || userRegistrationRequestDto.getPassword() == null
                || userRegistrationRequestDto.getRepeatPassword() == null) {
            return false;
        }
        return userRegistrationRequestDto.getPassword()
                .equals(userRegistrationRequestDto.getRepeatPassword());
    }
}
