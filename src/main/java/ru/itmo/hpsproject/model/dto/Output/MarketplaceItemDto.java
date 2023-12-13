package ru.itmo.hpsproject.model.dto.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.hpsproject.model.enums.Rarity;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemDto {
    Long id;
    String name;
    Rarity rarity;
    int price;
}
