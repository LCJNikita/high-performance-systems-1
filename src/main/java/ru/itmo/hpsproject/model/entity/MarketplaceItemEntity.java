package ru.itmo.hpsproject.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "marketplace_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    private int price;
}
