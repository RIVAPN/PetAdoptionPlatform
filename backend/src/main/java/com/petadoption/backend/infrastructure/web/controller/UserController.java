package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.User;
import com.petadoption.backend.core.domain.UserService;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import com.petadoption.backend.infrastructure.web.dto.UpdateUserRequest;
import com.petadoption.backend.infrastructure.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // CRIAÇÃO (pública)
    // =========================
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User created = userService.createUser(request);
        UserResponse response = UserResponse.fromDomain(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // BUSCA POR ID (para uso interno / admin futuramente)
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.ok(response);
    }

    // =========================
    // ENDPOINTS "ME" (usuário autenticado)
    // =========================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        String email = getAuthenticatedEmail();
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        String email = getAuthenticatedEmail();
        User updated = userService.updateSelf(email, request);
        return ResponseEntity.ok(UserResponse.fromDomain(updated));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe() {
        String email = getAuthenticatedEmail();
        userService.deleteSelf(email);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // Helper para pegar o e-mail do usuário logado
    // =========================
    private String getAuthenticatedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        // No nosso JWT, o "username" é o email
        return auth.getName();
    }
}