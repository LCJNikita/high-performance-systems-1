package ru.itmo.hpsproject.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByUser(UserEntity user);

    default Optional<ItemEntity> deleteItemById(Long itemId) {
        Optional<ItemEntity> optionalItem = findById(itemId);

        if (optionalItem.isPresent()) {
            deleteById(itemId);
        }

        return optionalItem;
    }
}
