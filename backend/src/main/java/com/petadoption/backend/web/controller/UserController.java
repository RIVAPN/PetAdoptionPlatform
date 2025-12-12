package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.User;
import com.petadoption.backend.core.domain.UserService;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import com.petadoption.backend.infrastructure.web.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        User created = userService.createUser(request);

        UserResponse response = new UserResponse(
                created.getId(),
                created.getName(),
                created.getEmail(),
                created.getPhone(),
                created.getAddress()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );

        return ResponseEntity.ok(response);
    }
}