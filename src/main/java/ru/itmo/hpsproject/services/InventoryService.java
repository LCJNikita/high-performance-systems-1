package ru.itmo.hpsproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exeptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.repositories.UsersRepository;

import java.util.List;

@Service
public class InventoryService {

    private final ItemsRepository itemsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public InventoryService(ItemsRepository itemsRepository, UsersRepository usersRepository) {
        this.itemsRepository = itemsRepository;
        this.usersRepository = usersRepository;
    }

    public List<ItemEntity> findUserInventory(Long userId) throws NotFoundException {
        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id: " + userId + " не найден"));

        return itemsRepository.findByUser(user);
    }
}
