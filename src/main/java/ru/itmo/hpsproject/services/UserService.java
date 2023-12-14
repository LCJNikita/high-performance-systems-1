package ru.itmo.hpsproject.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.exceptions.UserAlreadyExistsException;
import ru.itmo.hpsproject.model.dto.UserRegisterRequestDto;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    public void deleteUser(Long id) throws NotFoundException {
        if (!existsById(id)) throw new NotFoundException("Пользователь с id " + id + " не найден");
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserEntity getByUsername(String username) {
        return userRepository.getUserEntityByUsername(username);
    }

    public void updateBalance(Long userId, Integer newBalance) throws NotFoundException {
        UserEntity user = findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        user.setBalance(newBalance);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("Пользователь с именем '%s' не найден", username)));
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public UserEntity register(@Valid UserRegisterRequestDto userRegisterRequest) throws UserAlreadyExistsException {
        if (existsByUsername(userRegisterRequest.getUsername())) {
            throw new UserAlreadyExistsException(String.format("Пользователь с именем '%s' уже существует", userRegisterRequest.getUsername()));
        }
        UserEntity user = UserEntity.builder()
                .username(userRegisterRequest.getUsername())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .email(userRegisterRequest.getEmail())
                .balance(0)
                .roles(List.of(roleService.getStandardUserRole()))
                .build();
        userRepository.save(user);
        return user;
    }
}
