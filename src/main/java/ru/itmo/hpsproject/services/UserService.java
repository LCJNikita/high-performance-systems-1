package ru.itmo.hpsproject.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.NotAllowedException;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.exceptions.UserAlreadyExistsException;
import ru.itmo.hpsproject.exceptions.UserBlockedException;
import ru.itmo.hpsproject.model.dto.Output.UserDto;
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

    private final ModelMapper mapper;

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

    public void updateBalance(Long userId, Integer newBalance) throws NotFoundException {
        UserEntity user = findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    public void replenishBalance(Long id, Integer sum, String username) throws NotFoundException, NotAllowedException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        if (!user.getUsername().equals(username)) throw new NotAllowedException("Операция недоступна");
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
    }

    public List<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream().map((user) -> (mapper.map(user, UserDto.class))).toList();
    }

    public void setAdminRole(Long id) throws NotFoundException, UserBlockedException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) throw new UserBlockedException("Пользователь не может быть админом, так как он в черном списке");
        if (!user.getRoles().contains(roleService.getAdminRole())) {
            user.addRole(roleService.getAdminRole());
            userRepository.save(user);
        }
    }

    public void removeAdminRole(Long id) throws NotFoundException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        user.removeRole(roleService.getAdminRole());
        userRepository.save(user);
    }

    public void setPremiumUserRole(Long id) throws NotFoundException, UserBlockedException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) throw new UserBlockedException("Пользователь не может быть премиумом, так как он в черном списке");
        if (!user.getRoles().contains(roleService.getPremiumUserRole())) {
            user.removeRole(roleService.getStandardUserRole());
            user.addRole(roleService.getPremiumUserRole());
            userRepository.save(user);
        }
    }

    public void setBlockedUserRole(Long id) throws NotFoundException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        user.clearRoles();
        user.addRole(roleService.getBlockedUserRole());
        userRepository.save(user);
    }

    public void setStandardUserRole(Long id) throws NotFoundException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) {
            user.removeRole(roleService.getBlockedUserRole());
            user.addRole(roleService.getStandardUserRole());
        }
        if (user.getRoles().contains(roleService.getPremiumUserRole())) {
            user.removeRole(roleService.getPremiumUserRole());
            user.addRole(roleService.getStandardUserRole());
        }
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
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).collect(Collectors.toList())
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
