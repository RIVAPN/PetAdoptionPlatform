package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    // vamos sobrescrever o passwordEncoder para não depender do real
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_deveLancarExcecaoQuandoEmailJaExiste() {
        // arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Pedro");
        request.setEmail("pedro@example.com");
        request.setPassword("senha123");
        request.setPhone("123");
        request.setAddress("Rua X");

        when(userRepository.existsByEmail("pedro@example.com")).thenReturn(true);

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Email já está em uso", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_deveSalvarUserComSenhaCriptografadaERoleAdopter() {
        // arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Pedro");
        request.setEmail("pedro@example.com");
        request.setPassword("senha123");
        request.setPhone("123");
        request.setAddress("Rua X");

        when(userRepository.existsByEmail("pedro@example.com")).thenReturn(false);

        Role adopterRole = new Role();
        adopterRole.setName("ROLE_ADOPTER");
        when(roleRepository.findByName("ROLE_ADOPTER")).thenReturn(Optional.of(adopterRole));

        // para inspecionar o user salvo
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        User created = userService.createUser(request);

        // assert
        assertEquals("Pedro", created.getName());
        assertEquals("pedro@example.com", created.getEmail());
        assertNotNull(created.getPasswordHash());
        assertNotEquals("senha123", created.getPasswordHash()); // senha não pode estar em texto puro
        assertFalse(created.getRoles().isEmpty());
        assertTrue(created.getRoles().stream().anyMatch(r -> "ROLE_ADOPTER".equals(r.getName())));

        // sanity check: o hash bate com a senha usando o mesmo encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("senha123", created.getPasswordHash()));
    }
}