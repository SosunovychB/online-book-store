package book.store.service;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserRegistrationResponseDto;
import book.store.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;
}
