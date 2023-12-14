package ru.itmo.hpsproject.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.hpsproject.exceptions.NotEnoughMoneyException;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.dto.Input.BuyMarketplaceItemRequestDto;
import ru.itmo.hpsproject.model.dto.Input.SellMarketplaceItemRequestDto;
import ru.itmo.hpsproject.model.dto.Output.MarketplaceItemDto;
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
            @Positive @RequestParam(defaultValue = "1") int minPrice,
            @Positive @RequestParam(defaultValue = "1000000") int maxPrice,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
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

        PageRequest pageRequest = sort != null
                ? PageRequest.of(page, ControllersConstants.PAGE_SIZE, sort)
                : PageRequest.of(page, ControllersConstants.PAGE_SIZE);

        Page<MarketplaceItemEntity> resultPage = marketplaceService.findAll(
                minPrice,
                maxPrice,
                pageRequest
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
            @Valid @RequestBody SellMarketplaceItemRequestDto requestDto
    ) {
        try {
            MarketplaceItemEntity entity = marketplaceService.createMarketplaceItem(
                    requestDto.getItemId(),
                    requestDto.getPrice()
            );
            return ResponseEntity.ok(DtoConverter.marketplaceItemEntityToDto(entity));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyMarketplaceItem(
            @Valid @RequestBody BuyMarketplaceItemRequestDto requestDto
    ) {
        try {
            marketplaceService.purchaseMarketplaceItem(requestDto.getBuyerId(), requestDto.getItemId());
            return ResponseEntity.ok("Покупка совершена успешно");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotEnoughMoneyException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-marketplace-item/{itemId}")
    public ResponseEntity<?> deleteItemById(@PathVariable @Positive Long itemId) {
        Optional<MarketplaceItemEntity> marketplaceItemEntity = marketplaceService.deleteMarketplaceItemById(itemId);
        if (marketplaceItemEntity.isPresent()) {
            return ResponseEntity.ok("Айтем успешно удален");
        } else {
            return ResponseEntity.badRequest().body("Айтем с id: " + itemId + " не найден");
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAll() {
        marketplaceService.deleteAllMarketplaceItems();
        return ResponseEntity.ok("Все айтемы успешно удалены");
    }
}
