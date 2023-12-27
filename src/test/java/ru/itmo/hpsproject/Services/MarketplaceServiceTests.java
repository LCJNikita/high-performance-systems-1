package ru.itmo.hpsproject.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hpsproject.exceptions.NotEnoughMoneyException;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.model.enums.Rarity;
import ru.itmo.hpsproject.repositories.MarketplaceItemsRepository;
import ru.itmo.hpsproject.services.ItemsService;
import ru.itmo.hpsproject.services.MarketplaceService;
import ru.itmo.hpsproject.services.UserService;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MarketplaceServiceTests {

    @MockBean
    private MarketplaceItemsRepository marketplaceRepository;

    @MockBean
    private ItemsService itemsService;

    @MockBean
    private UserService userService;

    @Autowired
    private MarketplaceService marketplaceService;

    @Test
    void findAll_ReturnsPageOfMarketplaceItems() {
        // Given
        int minPrice = 0;
        int maxPrice = 100;
        Pageable pageable = PageRequest.of(0, 10);
        ItemEntity item = new ItemEntity(1L, "Sword", Rarity.STANDART, null);
        MarketplaceItemEntity sampleMarketplaceItem = new MarketplaceItemEntity(1L, item, 50);
        Page<MarketplaceItemEntity> expectedPage = new PageImpl<>(Collections.singletonList(sampleMarketplaceItem));
        when(marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(expectedPage);

        // When
        Page<MarketplaceItemEntity> resultPage = marketplaceService.findAll(minPrice, maxPrice, pageable);

        // Then
        assertEquals(expectedPage, resultPage);
        verify(marketplaceRepository, times(1)).findAllByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Test
    void findMarketplaceItemsByName_ReturnsListofMarketplaceItems() {
        // Given
        String itemName = "Sword";
        ItemEntity item = new ItemEntity(1L, "Sword", Rarity.STANDART, null);
        MarketplaceItemEntity marketplaceItem = new MarketplaceItemEntity(1L, item, 50);
        List<MarketplaceItemEntity> expectedItems = Collections.singletonList(marketplaceItem);
        when(marketplaceRepository.findByItemNameStartingWith(itemName)).thenReturn(expectedItems);

        // When
        List<MarketplaceItemEntity> resultItems = marketplaceService.findMarketplaceItemsByName(itemName);

        // Then
        assertEquals(expectedItems, resultItems);
        verify(marketplaceRepository, times(1)).findByItemNameStartingWith(itemName);
    }

    @Test
    void findMarketplaceItemsByUser_ReturnsListofMarketplaceItems() throws NotFoundException {
        // Given
        String userName = "seller";
        UserEntity seller = new UserEntity(1L, "seller@email.com", "sellerPass", userName, "Seller description", 1000, null, null);

        ItemEntity sampleItem = new ItemEntity(1L, "Sword", Rarity.STANDART, seller);
        MarketplaceItemEntity sampleMarketplaceItem = new MarketplaceItemEntity(1L, sampleItem, 50);

        List<MarketplaceItemEntity> expectedItems = Collections.singletonList(sampleMarketplaceItem);

        when(userService.findByUsername(userName)).thenReturn(Optional.of(seller));
        when(marketplaceRepository.findByItemUser(seller)).thenReturn(expectedItems);

        // When
        List<MarketplaceItemEntity> resultItems = marketplaceService.findMarketplaceItemsByUser(userName);

        // Then
        assertEquals(expectedItems, resultItems);
        verify(userService, times(1)).findByUsername(userName);
        verify(marketplaceRepository, times(1)).findByItemUser(seller);
    }

    @Test
    void purchaseMarketplaceItem_SuccessfullyPurchasesItem() throws NotFoundException, NotEnoughMoneyException, IllegalArgumentException {
        // Given
        String buyerName = "buyer";
        Long marketplaceItemId = 1L;
        int itemPrice = 50;

        UserEntity buyer = new UserEntity(2L, "buyer@email.com", "buyerPass", buyerName, "Buyer description", 500, null, null);
        UserEntity seller = new UserEntity(1L, "seller@email.com", "sellerPass", "seller", "Seller description", 1000, null, null);

        ItemEntity itemEntity = new ItemEntity(3L, "Sword", Rarity.STANDART, seller);
        MarketplaceItemEntity marketplaceItem = new MarketplaceItemEntity(marketplaceItemId, itemEntity, itemPrice);

        when(userService.findByUsername(buyerName)).thenReturn(Optional.of(buyer));
        when(marketplaceRepository.findById(marketplaceItemId)).thenReturn(Optional.of(marketplaceItem));

        // When
        marketplaceService.purchaseMarketplaceItem(buyerName, marketplaceItemId);

        // Then
        verify(userService, times(1)).findByUsername(buyerName);
        verify(marketplaceRepository, times(1)).findById(marketplaceItemId);
        verify(userService, times(1)).updateBalance(eq(seller.getId()), anyInt());
        verify(userService, times(1)).updateBalance(eq(buyer.getId()), anyInt());
        verify(itemsService, times(1)).updateUserId(eq(itemEntity.getId()), eq(buyer.getId()));
        verify(marketplaceRepository, times(1)).deleteById(marketplaceItemId);
    }

    @Test
    void deleteMarketplaceItemById_WithValidItemId_ReturnsDeletedItem() {
        // Given
        Long marketplaceItemId = 1L;
        MarketplaceItemEntity expectedItem = new MarketplaceItemEntity(marketplaceItemId, null, 50);
        when(marketplaceRepository.deleteMarketplaceItemById(marketplaceItemId)).thenReturn(Optional.of(expectedItem));

        // When
        Optional<MarketplaceItemEntity> deletedItem = marketplaceService.deleteMarketplaceItemById(marketplaceItemId);

        // Then
        assertTrue(deletedItem.isPresent());
        assertEquals(expectedItem, deletedItem.get());
        verify(marketplaceRepository, times(1)).deleteMarketplaceItemById(marketplaceItemId);
    }

    @Test
    void deleteMarketplaceItemById_WithInvalidItemId_ReturnsEmptyOptional() {
        // Given
        Long invalidItemId = 999L;
        when(marketplaceRepository.deleteMarketplaceItemById(invalidItemId)).thenReturn(Optional.empty());

        // When
        Optional<MarketplaceItemEntity> deletedItem = marketplaceService.deleteMarketplaceItemById(invalidItemId);

        // Then
        assertFalse(deletedItem.isPresent());
        verify(marketplaceRepository, times(1)).deleteMarketplaceItemById(invalidItemId);
    }

    @Test
    void deleteAllMarketplaceItems_DeletesAllItems() {
        // When
        marketplaceService.deleteAllMarketplaceItems();

        // Then
        verify(marketplaceRepository, times(1)).deleteAll();
    }
}
