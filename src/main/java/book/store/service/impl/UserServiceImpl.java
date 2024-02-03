package book.store.service.impl;

import book.store.config.SecurityConfig;
import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserRegistrationResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.role.RoleRepository;
import book.store.repository.user.UserRepository;
import book.store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityConfig securityConfig;
    private final RoleRepository roleRepository;

    @Override
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto userRegistrationRequestDto) throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with the email "
                    + userRegistrationRequestDto.getEmail() + " already exists. Use another one.");
        }
        User user = userMapper.toModel(userRegistrationRequestDto);
        user.setPassword(securityConfig.getPasswordEncoder()
                .encode(userRegistrationRequestDto.getPassword()));
        user.getRoles().add(roleRepository.findByRoleName(Role.RoleName.ROLE_USER));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }
}
