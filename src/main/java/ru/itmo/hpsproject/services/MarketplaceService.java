package ru.itmo.hpsproject.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.NotEnoughMoneyException;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.MarketplaceItemsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceItemsRepository marketplaceRepository;

    private final ItemsService itemsService;
    private final UserService userService;

    public Page<MarketplaceItemEntity> findAll(int minPrice, int maxPrice, Pageable pageable) {
        return marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByName(String name) {
        return marketplaceRepository.findByItemNameStartingWith(name);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByUser(String userName) throws NotFoundException {
        UserEntity user = userService.findByUsername(userName)
                .orElseThrow(() -> new NotFoundException("User with name " + userName + " not found"));
        return marketplaceRepository.findByItemUser(user);
    }

    public MarketplaceItemEntity createMarketplaceItem(
            String userName,
            Long itemId,
            int price
    ) throws NotFoundException, IllegalArgumentException {
        ItemEntity itemEntity = itemsService.findItemById(itemId);

        if (marketplaceRepository.existsByItem(itemEntity)) {
            throw new IllegalArgumentException("Айтем с id " + itemId + " уже находится на торговой площадке");
        }

        UserEntity seller = userService.findByUsername(userName)
                .orElseThrow(() -> new NotFoundException("User with name " + userName + " not found"));

        if (!seller.getItems().contains(itemEntity)) {
            throw new IllegalArgumentException("Item with id " + itemId + " is not item of user");
        }

        MarketplaceItemEntity marketplaceItemEntity = new MarketplaceItemEntity();
        marketplaceItemEntity.setItem(itemEntity);
        marketplaceItemEntity.setPrice(price);
        return marketplaceRepository.save(marketplaceItemEntity);
    }
    @Transactional
    public void purchaseMarketplaceItem(String buyerUserName, Long marketplaceItemId) throws NotFoundException, NotEnoughMoneyException, IllegalArgumentException {
        UserEntity buyer = userService.findByUsername(buyerUserName)
                .orElseThrow(() -> new NotFoundException("Buyer with userName " + buyerUserName + " not found"));

        MarketplaceItemEntity marketplaceItem = marketplaceRepository.findById(marketplaceItemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + marketplaceItemId + " not found on the marketplace"));

        if (marketplaceItem.getItem().getUser().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("Нельзя купить айтем у самого себя");
        }

        if (buyer.getBalance() < marketplaceItem.getPrice()) {
            throw new NotEnoughMoneyException();
        }

        UserEntity seller = marketplaceItem.getItem().getUser();

        userService.updateBalance(seller.getId(), seller.getBalance() + marketplaceItem.getPrice());

        userService.updateBalance(buyer.getId(), buyer.getBalance() - marketplaceItem.getPrice());

        ItemEntity item = marketplaceItem.getItem();

        itemsService.updateUserId(item.getId(), buyer.getId());

        marketplaceRepository.deleteById(marketplaceItemId);
    }

    public Optional<MarketplaceItemEntity> deleteMarketplaceItemById(Long itemId) {
        return marketplaceRepository.deleteMarketplaceItemById(itemId);
    }

    public void deleteAllMarketplaceItems() {
        marketplaceRepository.deleteAll();
    }
}
