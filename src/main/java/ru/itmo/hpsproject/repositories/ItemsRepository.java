package ru.itmo.hpsproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hpsproject.model.entity.ItemEntity;
@Repository
public interface ItemsRepository extends JpaRepository<ItemEntity, Long> {
    // получить предметы пользователя (по id пользователя - инвентарь)
    // добавить предмет
    // удалить предмет
    // обновить какое-то поле предмета
    // получить предмет по id
}
