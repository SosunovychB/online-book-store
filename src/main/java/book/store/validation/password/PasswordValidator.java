package book.store.validation.password;

import book.store.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordValidator
        implements ConstraintValidator<PasswordMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto userRegistrationRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(userRegistrationRequestDto.getPassword(),
                userRegistrationRequestDto.getRepeatPassword());
    }
}
