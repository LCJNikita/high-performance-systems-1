package ru.itmo.hpsproject.controllers;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.model.dto.Output.ItemDto;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.services.InventoryService;
import ru.itmo.hpsproject.utils.DtoConverter;

import java.util.List;

@RequestMapping(path = "/inventory")
@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Positive @RequestParam Long userId) {
        try {
            List<ItemEntity> inventoryItemsEntities = inventoryService.findUserInventory(userId);
            List<ItemDto> inventoryItemsDtos = inventoryItemsEntities
                    .stream()
                    .map(DtoConverter::itemEntityToDto)
                    .toList();

            return ResponseEntity.ok(inventoryItemsDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
