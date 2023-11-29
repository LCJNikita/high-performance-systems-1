package ru.itmo.hpsproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exeptions.NotEnoughMoneyException;
import ru.itmo.hpsproject.exeptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.repositories.MarketplaceItemsRepository;
import ru.itmo.hpsproject.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MarketplaceService {

    private final MarketplaceItemsRepository marketplaceRepository;
    private final ItemsRepository itemsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public MarketplaceService(
            MarketplaceItemsRepository marketplaceRepository,
            ItemsRepository itemsRepository,
            UsersRepository usersRepository
    ) {
        this.marketplaceRepository = marketplaceRepository;
        this.itemsRepository = itemsRepository;
        this.usersRepository = usersRepository;
    }

    public Page<MarketplaceItemEntity> findAll(int minPrice, int maxPrice, Pageable pageable) {
        return marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByName(String name) {
        return marketplaceRepository.findByItemNameStartingWith(name);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByUser(Long userId) throws NotFoundException {
        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return marketplaceRepository.findByItemUser(user);
    }

    public MarketplaceItemEntity createMarketplaceItem(Long itemId, int price) throws NotFoundException, IllegalArgumentException {
        ItemEntity itemEntity = itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        if (marketplaceRepository.existsByItem(itemEntity)) {
            throw new IllegalArgumentException("Item with id " + itemId + " is already listed on the marketplace");
        }

        MarketplaceItemEntity marketplaceItemEntity = new MarketplaceItemEntity();
        marketplaceItemEntity.setItem(itemEntity);
        marketplaceItemEntity.setPrice(price);
        return marketplaceRepository.save(marketplaceItemEntity);
    }

    public void purchaseMarketplaceItem(Long buyerId, Long marketplaceItemId) throws NotFoundException, NotEnoughMoneyException {
        UserEntity buyer = usersRepository.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("Buyer with id " + buyerId + " not found"));

        MarketplaceItemEntity marketplaceItem = marketplaceRepository.findById(marketplaceItemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + marketplaceItemId + " not found on the marketplace"));

        if (buyer.getBalance() < marketplaceItem.getPrice()) {
            throw new NotEnoughMoneyException();
        }

        UserEntity seller = marketplaceItem.getItem().getUser();
        seller.setBalance(seller.getBalance() + marketplaceItem.getPrice());
        usersRepository.save(seller);

        buyer.setBalance(buyer.getBalance() - marketplaceItem.getPrice());
        usersRepository.save(buyer);

        ItemEntity item = marketplaceItem.getItem();
        item.setUser(buyer);
        itemsRepository.save(item);

        marketplaceRepository.deleteById(marketplaceItemId);
    }

    public Optional<MarketplaceItemEntity> deleteMarketplaceItemById(Long itemId) {
        return marketplaceRepository.deleteMarketplaceItemById(itemId);
    }

    public void deleteAllMarketplaceItems() {
        marketplaceRepository.deleteAll();
    }
}
