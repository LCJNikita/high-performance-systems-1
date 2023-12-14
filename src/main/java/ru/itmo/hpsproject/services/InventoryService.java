package ru.itmo.hpsproject.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemsRepository itemsRepository;
    private final UserService userService;

    public List<ItemEntity> findUserInventory(Long userId) throws NotFoundException {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id: " + userId + " не найден"));

        return itemsRepository.findByUser(user);
    }
}
