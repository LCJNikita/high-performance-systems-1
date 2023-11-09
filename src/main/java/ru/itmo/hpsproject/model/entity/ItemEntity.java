package ru.itmo.hpsproject.model.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemEntity {
    private String title;
    private int price;
}
