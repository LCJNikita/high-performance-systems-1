package ru.itmo.hpsproject.utils;

import ru.itmo.hpsproject.model.dto.ItemDto;
import ru.itmo.hpsproject.model.dto.MarketplaceItemDto;
import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;

public class DtoConverter {

    public static ItemDto itemEntityToDto(ItemEntity itemEntity) {
        return new ItemDto(itemEntity.getId(), itemEntity.getName(), itemEntity.getRarity());
    }

    public static MarketplaceItemDto marketplaceItemEntityToDto(MarketplaceItemEntity marketplaceItemEntity) {
        return new MarketplaceItemDto(
                marketplaceItemEntity.getId(),
                marketplaceItemEntity.getItem().getName(),
                marketplaceItemEntity.getItem().getRarity(),
                marketplaceItemEntity.getPrice()
        );
    }
}
