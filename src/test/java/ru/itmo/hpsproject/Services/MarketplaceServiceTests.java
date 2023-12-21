package ru.itmo.hpsproject.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.repositories.MarketplaceItemsRepository;
import ru.itmo.hpsproject.services.MarketplaceService;
import ru.itmo.hpsproject.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

//@Testcontainers
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MarketplaceServiceTests {

    @InjectMocks
    private MarketplaceService marketplaceService;
    @Mock
    private MarketplaceItemsRepository marketplaceRepository;
    @Mock
    private UserService userService;

    @Test
    public void testFindAll() {

        int minPrice = 0;
        int maxPrice = 100;
        Pageable pageable = PageRequest.of(0, 10);

        List<MarketplaceItemEntity> items = Arrays.asList();
        Page<MarketplaceItemEntity> expectedPage = new PageImpl<>(items, pageable, items.size());

        when(marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(expectedPage);

        Page<MarketplaceItemEntity> resultPage = marketplaceService.findAll(minPrice, maxPrice, pageable);

        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void testFindMarketplaceItemsByName() {

        String itemName = "testItem";
        List<MarketplaceItemEntity> expectedItems = Arrays.asList();

        when(marketplaceRepository.findByItemNameStartingWith(itemName)).thenReturn(expectedItems);

        List<MarketplaceItemEntity> resultItems = marketplaceService.findMarketplaceItemsByName(itemName);

        assertEquals(expectedItems, resultItems);
    }

//    @Test
//    public void testFindMarketplaceItemsByUser() throws NotFoundException {
//
//        String userName = "testUser";
//        UserEntity userEntity = new UserEntity(1L, "email", "pass", userName, 100, null, null);
//        List<MarketplaceItemEntity> expectedItems = Arrays.asList();
//
//        when(userService.findByUsername(userName)).thenReturn(Optional.of(userEntity));
//        when(marketplaceRepository.findByItemUser(userEntity)).thenReturn(expectedItems);
//
//        List<MarketplaceItemEntity> resultItems = marketplaceService.findMarketplaceItemsByUser(userName);
//
//        assertEquals(expectedItems, resultItems);
//    }
}
