package ru.itmo.hpsproject.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    private String email;
    private String password;
    private String username;
    private Integer balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<RoleEntity> roles;

//    @ManyToMany
//    @JoinTable(
//            name = "friends",
//            joinColumns = @JoinColumn(name = "first_user_id", referencedColumnName = "user_id", nullable = false),
//            inverseJoinColumns = @JoinColumn(name = "second_user_id", referencedColumnName = "user_id", nullable = false)
//    )
//    private Collection<UserEntity> friends;

}
