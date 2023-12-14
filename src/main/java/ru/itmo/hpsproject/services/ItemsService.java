package ru.itmo.hpsproject.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.model.enums.Rarity;
import ru.itmo.hpsproject.repositories.ItemsRepository;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsRepository itemsRepository;
    private final UserService userService;

    public ItemEntity generateRandomItemForUser(Long userId) throws NotFoundException {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id: " + userId + " не найден"));

        ItemEntity randomItem = createRandomItem();
        randomItem.setUser(user);
        return itemsRepository.save(randomItem);
    }

    public ItemEntity findItemById(Long itemId) throws NotFoundException {
        return itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Айтем с id: " + itemId + " не найден"));
    }

    public Optional<ItemEntity> deleteItemById(Long itemId) {
        return itemsRepository.deleteItemById(itemId);
    }

    public void deleteAllItems() {
        itemsRepository.deleteAll();
    }

    public void updateUserId(Long itemId, Long newUserId) throws NotFoundException {
        ItemEntity item = findItemById(itemId);

        UserEntity newUser = userService.findById(newUserId)
                .orElseThrow(() -> new NotFoundException("User with id " + newUserId + " not found"));

        item.setUser(newUser);
        itemsRepository.save(item);
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
