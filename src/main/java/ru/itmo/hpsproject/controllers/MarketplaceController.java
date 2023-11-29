package ru.itmo.hpsproject.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.hpsproject.exeptions.NotEnoughMoneyException;
import ru.itmo.hpsproject.exeptions.NotFoundException;
import ru.itmo.hpsproject.model.dto.MarketplaceItemDto;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;
import ru.itmo.hpsproject.services.MarketplaceService;
import ru.itmo.hpsproject.utils.ControllersConstants;
import ru.itmo.hpsproject.utils.DtoConverter;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/marketplace")
@RestController
public class MarketplaceController {
    private final MarketplaceService marketplaceService;

    @Autowired
    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping
    public ResponseEntity<List<MarketplaceItemDto>> getAll(
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "1000000") int maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortOrder
    ) {
        Sort sort = null;
        if (sortOrder != null) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                sort = Sort.by(Sort.Order.asc("price"));
            } else if (sortOrder.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Order.desc("price"));
            }
        }

        Page<MarketplaceItemEntity> resultPage = marketplaceService.findAll(
                minPrice,
                maxPrice,
                PageRequest.of(page, ControllersConstants.PAGE_SIZE, sort)
        );

        List<MarketplaceItemDto> resultBody = resultPage.getContent()
                .stream()
                .map(DtoConverter::marketplaceItemEntityToDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultPage.getTotalElements()))
                .body(resultBody);
    }

    @GetMapping("/search")
    public List<MarketplaceItemDto> getMarketplaceItemsByName(@RequestParam String itemName) {
        List<MarketplaceItemEntity> marketplaceItemsEntities = marketplaceService.findMarketplaceItemsByName(itemName);
        return marketplaceItemsEntities
                .stream()
                .map(DtoConverter::marketplaceItemEntityToDto)
                .toList();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMarketplaceItemsByUser(@RequestParam Long userId) {
        try {
            List<MarketplaceItemEntity> marketplaceItemsEntities = marketplaceService.findMarketplaceItemsByUser(userId);
            List<MarketplaceItemDto> marketplaceItemsDtos = marketplaceItemsEntities
                    .stream()
                    .map(DtoConverter::marketplaceItemEntityToDto)
                    .toList();
            return ResponseEntity.ok(marketplaceItemsDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellMarketplaceItem(
            @RequestParam @Positive Long itemId,
            @RequestParam @Positive @Max(1000000) int price
    ) {
        try {
            MarketplaceItemEntity entity = marketplaceService.createMarketplaceItem(itemId, price);
            return ResponseEntity.ok(DtoConverter.marketplaceItemEntityToDto(entity));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyMarketplaceItem(
            @RequestParam @Positive Long buyerId,
            @RequestParam @Positive Long itemId
    ) {
        try {
            marketplaceService.purchaseMarketplaceItem(buyerId, itemId);
            return ResponseEntity.ok("Покупка совершена успешно");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotEnoughMoneyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-marketplace-item")
    public ResponseEntity<?> deleteItemById(@RequestBody @Positive Long itemId) {
        Optional<MarketplaceItemEntity> marketplaceItemEntity = marketplaceService.deleteMarketplaceItemById(itemId);
        if (marketplaceItemEntity.isPresent()) {
            return ResponseEntity.ok("Айтем успешно удален");
        } else {
            return ResponseEntity.badRequest().body("Айтем не найден");
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAll() {
        marketplaceService.deleteAllMarketplaceItems();
        return ResponseEntity.ok("Все айтемы успешно удалены");
    }
}
