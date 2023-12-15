package ru.itmo.hpsproject.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.hpsproject.exceptions.NotAllowedException;
import ru.itmo.hpsproject.exceptions.NotFoundException;
import ru.itmo.hpsproject.exceptions.UserBlockedException;
import ru.itmo.hpsproject.model.dto.Output.UserDto;
import ru.itmo.hpsproject.services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam @NotNull @Min(1) Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Пользователь с id " + userId + " успешно удален");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/replenish-balance/{userId}")
    public ResponseEntity<?> replenishBalance(Principal principal, @PathVariable @NotNull @Min(1) Long userId, @RequestParam @NotNull @Min(1) Integer sum) {
        try {
            String username = principal.getName();
            userService.replenishBalance(userId, sum, username);
            return ResponseEntity.ok("Баланас успешно пополнен");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage());
        }
    }

    @GetMapping
    public List<UserDto> getAllUsers(
            @PageableDefault(sort = {"id"}, size = 50) Pageable pageable
    ){
        return userService.getAll(pageable);
    }

    @PostMapping("/set-admin-role")
    public ResponseEntity<?> setAdminRole(@RequestParam @NotNull @Min(1) Long userId ) {
        try {
            userService.setAdminRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/remove-admin-role")
    public ResponseEntity<?> removeAdminRole(@RequestParam @NotNull @Min(1) Long userId ) {
        try {
            userService.removeAdminRole(userId);
            return ResponseEntity.ok("Роль успешно удалена");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set-premium-user-role")
    public ResponseEntity<?> setPremiumUserRole(@RequestParam @NotNull @Min(1) Long userId ) {
        try {
            userService.setPremiumUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set-blocked-user-role")
    public ResponseEntity<?> setBlockedUserRole(@RequestParam @NotNull @Min(1) Long userId ) {
        try {
            userService.setBlockedUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set-standard-user-role")
    public ResponseEntity<?> setStandardUserRole(@RequestParam @NotNull @Min(1) Long userId ) {
        try {
            userService.setStandardUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}


