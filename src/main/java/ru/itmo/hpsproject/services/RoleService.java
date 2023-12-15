package ru.itmo.hpsproject.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.model.entity.RoleEntity;
import ru.itmo.hpsproject.model.enums.Role;
import ru.itmo.hpsproject.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public RoleEntity findByRole(Role role) {
        return repository.findByRole(role).orElseThrow();
    }

    public RoleEntity getStandardUserRole() {
        return findByRole(Role.STANDARD_USER);
    }

    public RoleEntity getAdminRole() {
        return findByRole(Role.ADMIN);
    }
    public RoleEntity getPremiumUserRole() {
        return findByRole(Role.PREMIUM_USER);
    }
    public RoleEntity getBlockedUserRole() {
        return findByRole(Role.BLOCKED_USER);
    }

}
