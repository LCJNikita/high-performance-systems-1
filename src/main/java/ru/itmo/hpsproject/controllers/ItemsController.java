package ru.itmo.hpsproject.controllers;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.hpsproject.exeptions.NotFoundException;
import ru.itmo.hpsproject.model.dto.ItemDto;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.services.ItemsService;
import ru.itmo.hpsproject.utils.DtoConverter;

import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemsController {

    private final ItemsService itemsService;

    @Autowired
    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<?> generateRandomItem(@PathVariable @Positive Long userId) {
        try {
            ItemEntity itemEntity = itemsService.generateRandomItemForUser(userId);
            ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
            return ResponseEntity.ok(itemDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/item")
    public ResponseEntity<?> getItemById(@RequestBody @Positive Long itemId) {
        try {
            ItemEntity itemEntity = itemsService.findItemById(itemId);
            ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
            return ResponseEntity.ok(itemDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<?> deleteItemById(@PathVariable @Positive Long itemId) {
        Optional<ItemEntity> itemEntity = itemsService.deleteItemById(itemId);
        if (itemEntity.isPresent()) {
            return ResponseEntity.ok("Айтем успешно удален");
        } else {
            return ResponseEntity.badRequest().body("Айтем не найден");
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAll() {
        itemsService.deleteAllItems();
        return ResponseEntity.ok("Все айтемы успешно удалены");
    }
}
