package ru.itmo.hpsproject.model.dto.Input;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyMarketplaceItemRequestDto {

    @Positive
    private Long buyerId;

    @Positive
    private Long itemId;
}
