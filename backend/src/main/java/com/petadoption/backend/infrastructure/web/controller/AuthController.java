package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.User;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.security.JwtTokenService;
import com.petadoption.backend.infrastructure.web.dto.LoginRequest;
import com.petadoption.backend.infrastructure.web.dto.LoginResponse;
import com.petadoption.backend.infrastructure.web.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthController(UserJpaRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtTokenService.generateToken(user);
        UserResponse userResponse = UserResponse.fromDomain(user);

        LoginResponse response = new LoginResponse(token, userResponse);

        return ResponseEntity.ok(response);
    }
}