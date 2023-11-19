package ru.itmo.hpsproject.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hpsproject.model.dto.ItemDto;

import java.util.Collections;
import java.util.List;

@RequestMapping(path = "/inventory")
@RestController
public class InventoryController {

    @GetMapping
    public ResponseEntity<List<ItemDto>> userInventory(Long userId) {
        List<ItemDto> emptyList = Collections.emptyList();

        return new ResponseEntity<>(emptyList, HttpStatus.NO_CONTENT);
    }
}
