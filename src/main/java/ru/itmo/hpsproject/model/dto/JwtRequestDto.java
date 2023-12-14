package ru.itmo.hpsproject.model.dto;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
