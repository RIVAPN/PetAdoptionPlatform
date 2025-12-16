package com.petadoption.backend.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateOrganizationRequest {

    @NotBlank(message = "Nome da organização é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String name;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String description;

    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    private String phone;

    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String address;

    public CreateOrganizationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}