package ru.itmo.hpsproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.itmo.hpsproject.model.enums.Rarity;
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemDto {
    Long id;
    String name;
    Rarity rarity;
    int price;
}
