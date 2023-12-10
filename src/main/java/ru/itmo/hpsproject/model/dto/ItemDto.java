package ru.itmo.hpsproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.hpsproject.model.enums.Rarity;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    Long id;
    String name;
    Rarity rarity;
}
