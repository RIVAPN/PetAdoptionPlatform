package com.petadoption.backend.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddOrganizationMemberRequest {

    @NotBlank(message = "Email do usuário é obrigatório")
    @Email(message = "Email deve ser válido")
    private String userEmail;

    // "ADMIN" ou "STAFF"
    @NotBlank(message = "Papel é obrigatório")
    @Pattern(regexp = "ADMIN|STAFF", message = "Papel deve ser ADMIN ou STAFF")
    private String role;

    public AddOrganizationMemberRequest() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}