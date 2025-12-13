package com.petadoption.backend.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.backend.core.domain.User;
import com.petadoption.backend.core.domain.UserService;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import com.petadoption.backend.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // desliga filtros de segurança
@Import(GlobalExceptionHandler.class)     // inclui nosso handler de erros
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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