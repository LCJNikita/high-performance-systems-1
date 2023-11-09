package ru.itmo.hpsproject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEntity {
    private String login;
    private String password;
    private String email;
    private String name;
    private String description;
    private int balance;
}
