package com.petadoption.backend.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.backend.core.domain.User;
import com.petadoption.backend.core.domain.UserService;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import com.petadoption.backend.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        UserController controller = new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUser_deveRetornar201EUserResponse() throws Exception {
        // arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Pedro");
        request.setEmail("pedro@example.com");
        request.setPassword("senha123");
        request.setPhone("123456");
        request.setAddress("Rua X");

        User created = new User();
        created.setId(1L);
        created.setName("Pedro");
        created.setEmail("pedro@example.com");
        created.setPhone("123456");
        created.setAddress("Rua X");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(created);

        String json = objectMapper.writeValueAsString(request);

        // act + assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@example.com"));
    }

    @Test
    void createUser_quandoEmailJaExiste_deveRetornar400ComMensagemDeErro() throws Exception {
        // arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Pedro");
        request.setEmail("pedro@example.com");
        request.setPassword("senha123");
        request.setPhone("123456");
        request.setAddress("Rua X");

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Email já está em uso"));

        String json = objectMapper.writeValueAsString(request);

        // act + assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já está em uso"))
                .andExpect(jsonPath("$.status").value(400));
    }
}