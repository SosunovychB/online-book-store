package book.store.service.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.User;
import book.store.repository.user.UserRepository;
import book.store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with the email "
                    + userRegistrationRequestDto.getEmail() + " already exists. Use another one.");
        }
        User savedUser = userRepository.save(userMapper.toModel(userRegistrationRequestDto));
        return userMapper.toUserResponseDto(savedUser);
    }
}
