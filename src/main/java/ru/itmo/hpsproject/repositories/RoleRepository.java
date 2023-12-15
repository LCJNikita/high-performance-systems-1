package ru.itmo.hpsproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hpsproject.model.entity.RoleEntity;
import ru.itmo.hpsproject.model.enums.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByRole(Role role);
}
