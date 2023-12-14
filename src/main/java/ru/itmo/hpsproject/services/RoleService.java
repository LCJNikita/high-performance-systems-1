package ru.itmo.hpsproject.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.model.entity.RoleEntity;
import ru.itmo.hpsproject.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public RoleEntity findByName(String name) {
        return repository.findByName(name).orElseThrow();
    }

    public RoleEntity getStandardUserRole() {
        return findByName("STANDARD_USER");
    }


}
