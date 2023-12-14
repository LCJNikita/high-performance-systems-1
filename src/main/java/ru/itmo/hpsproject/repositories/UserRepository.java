package ru.itmo.hpsproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hpsproject.model.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> deleteByUsername(String username);

    void deleteById(Long id);
    boolean existsByUsername(String username);

    boolean existsById(Long id);

    UserEntity getUserEntityByUsername(String username);

}
