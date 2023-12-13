package ru.itmo.hpsproject.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.hpsproject.model.entity.UserEntity;
@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {

}
