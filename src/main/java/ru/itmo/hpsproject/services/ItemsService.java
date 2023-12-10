package ru.itmo.hpsproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exeptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.model.enums.Rarity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.repositories.UsersRepository;

import java.util.Optional;
import java.util.Random;

import static ru.itmo.hpsproject.model.enums.Rarity.*;

@Service
public class ItemsService {

    private final ItemsRepository itemsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ItemsService(ItemsRepository itemsRepository, UsersRepository usersRepository) {
        this.itemsRepository = itemsRepository;
        this.usersRepository = usersRepository;
    }

    public ItemEntity generateRandomItemForUser(Long userId) throws NotFoundException {
        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id: " + userId + " не найден"));

        ItemEntity randomItem = createRandomItem();
        randomItem.setUser(user);
        return itemsRepository.save(randomItem);
    }

    public ItemEntity findItemById(Long itemId) throws NotFoundException {
        return itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Айтем не найден"));
    }

    public Optional<ItemEntity> deleteItemById(Long itemId) {
        return itemsRepository.deleteItemById(itemId);
    }

    public void deleteAllItems() {
        itemsRepository.deleteAll();
    }

    // MARK: - Private
    private ItemEntity createRandomItem() {
        String[] titles = {"Sword", "Axe", "Pick", "Archery"};
        Rarity[] rarities = Rarity.values();

        Random random = new Random();

        String randomTitle = titles[random.nextInt(titles.length)];
        Rarity randomRarity = rarities[random.nextInt(rarities.length)];

        return new ItemEntity(null, randomTitle, randomRarity, null);
    }
}
