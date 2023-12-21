package ru.itmo.hpsproject.Services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.model.enums.Rarity;
import ru.itmo.hpsproject.repositories.ItemsRepository;
import ru.itmo.hpsproject.services.InventoryService;
import ru.itmo.hpsproject.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//@Testcontainers
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InventoryServiceTests {

    @Mock
    private ItemsRepository itemsRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void findUserInventory_WithValidUserId_ReturnsUserInventory() throws NotFoundException {
        // Given
        Long userId = 1L;

        UserEntity user = new UserEntity(userId, "email", "pass", "username", 100, "description", null, null);
        ItemEntity item1 = new ItemEntity(1L, "Item1", Rarity.STANDART, user);
        ItemEntity item2 = new ItemEntity(2L, "Item2", Rarity.RARE, user);
        List<ItemEntity> expectedInventory = Arrays.asList(item1, item2);

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(itemsRepository.findByUser(user)).thenReturn(expectedInventory);

        // When
        List<ItemEntity> actualInventory = inventoryService.findUserInventory(userId);

        // Then
        assertEquals(expectedInventory, actualInventory);
        verify(userService, times(1)).findById(userId);
        verify(itemsRepository, times(1)).findByUser(user);
    }

    @Test
    void findUserInventory_WithInvalidUserId_ThrowsNotFoundException() {
        // Arrange
        Long invalidUserId = 999L;

        when(userService.findById(invalidUserId)).thenReturn(Optional.empty());

        // Act and Assert
        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> inventoryService.findUserInventory(invalidUserId)
        );

        assertEquals("Юзер с id: 999 не найден", exception.getMessage());
        verify(userService, times(1)).findById(invalidUserId);
        verifyNoInteractions(itemsRepository);
    }
}
