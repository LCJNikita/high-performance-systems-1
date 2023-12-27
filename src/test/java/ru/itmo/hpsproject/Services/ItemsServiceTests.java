package ru.itmo.hpsproject.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.model.enums.Rarity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.services.ItemsService;
import ru.itmo.hpsproject.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemsServiceTests {

    @MockBean
    private ItemsRepository itemsRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private ItemsService itemsService;

    @Test
    void generateRandomItemForUser_WithValidUserId_ReturnsGeneratedItem() throws NotFoundException {
        // Given
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "email", "pass", "username", "description", 100, null, null);
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        ItemEntity randomItem = new ItemEntity(1L, "Sword", Rarity.STANDART, user);
        when(itemsRepository.save(any(ItemEntity.class))).thenReturn(randomItem);

        // When
        ItemEntity generatedItem = itemsService.generateRandomItemForUser(userId);

        // Then
        assertNotNull(generatedItem);
        assertEquals(user, generatedItem.getUser());
        verify(userService, times(1)).findById(userId);
        verify(itemsRepository, times(1)).save(any(ItemEntity.class));
    }

    @Test
    void generateRandomItemForUser_WithInvalidUserId_ThrowsNotFoundException() {
        // Given
        Long invalidUserId = 999L;
        when(userService.findById(invalidUserId)).thenReturn(Optional.empty());

        // When
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemsService.generateRandomItemForUser(invalidUserId)
        );

        // Then
        assertEquals("Юзер с id: 999 не найден", exception.getMessage());
        verify(userService, times(1)).findById(invalidUserId);
        verifyNoInteractions(itemsRepository);
    }

    @Test
    void findItemById_WithValidItemId_ReturnsItem() throws NotFoundException {
        // Given
        Long itemId = 1L;
        ItemEntity expectedItem = new ItemEntity(itemId, "Sword", Rarity.STANDART, null);
        when(itemsRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        // When
        ItemEntity foundItem = itemsService.findItemById(itemId);

        // Then
        assertNotNull(foundItem);
        assertEquals(expectedItem, foundItem);
        verify(itemsRepository, times(1)).findById(itemId);
    }

    @Test
    void findItemById_WithInvalidItemId_ThrowsNotFoundException() {
        // Given
        Long invalidItemId = 999L;
        when(itemsRepository.findById(invalidItemId)).thenReturn(Optional.empty());

        // When
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemsService.findItemById(invalidItemId)
        );

        // Then
        assertEquals("Айтем с id: 999 не найден", exception.getMessage());
        verify(itemsRepository, times(1)).findById(invalidItemId);
    }

    @Test
    void deleteItemById_WithValidItemId_ReturnsDeletedItem() throws NotFoundException {
        // Given
        Long itemId = 1L;
        ItemEntity expectedItem = new ItemEntity(itemId, "Sword", Rarity.STANDART, null);
        when(itemsRepository.deleteItemById(itemId)).thenReturn(Optional.of(expectedItem));

        // When
        Optional<ItemEntity> deletedItem = itemsService.deleteItemById(itemId);

        // Then
        assertTrue(deletedItem.isPresent());
        assertEquals(expectedItem, deletedItem.get());
        verify(itemsRepository, times(1)).deleteItemById(itemId);
    }

    @Test
    void deleteItemById_WithInvalidItemId_ReturnsEmptyOptional() {
        // Given
        Long invalidItemId = 999L;
        when(itemsRepository.deleteItemById(invalidItemId)).thenReturn(Optional.empty());

        // When
        Optional<ItemEntity> deletedItem = itemsService.deleteItemById(invalidItemId);

        // Then
        assertFalse(deletedItem.isPresent());
        verify(itemsRepository, times(1)).deleteItemById(invalidItemId);
    }

    @Test
    void deleteAllItems_DeletesAllItems() {
        // When
        itemsService.deleteAllItems();

        // Then
        verify(itemsRepository, times(1)).deleteAll();
    }
}
