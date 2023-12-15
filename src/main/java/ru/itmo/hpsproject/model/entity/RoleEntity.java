package ru.itmo.hpsproject.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.itmo.hpsproject.model.enums.Role;

@Entity
@Data
@Table(name="roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Role role;

}
