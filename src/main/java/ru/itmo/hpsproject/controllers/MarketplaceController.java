package ru.itmo.hpsproject.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hpsproject.model.dto.MarketplaceItemDto;

import java.util.Collections;
import java.util.List;

@RequestMapping(path = "/marketplace")
@RestController
public class MarketplaceController {

    @GetMapping
    public ResponseEntity<List<MarketplaceItemDto>> findAll(int page) {
        List<MarketplaceItemDto> emptyList = Collections.emptyList();

        return new ResponseEntity<>(emptyList, HttpStatus.NO_CONTENT);
    }
}
