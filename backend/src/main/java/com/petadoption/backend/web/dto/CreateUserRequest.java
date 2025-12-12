package com.petadoption.backend.infrastructure.web.dto;

public class CreateUserRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;

    // Construtor vazio é OBRIGATÓRIO para o Jackson
    public CreateUserRequest() {
    }

    // Getters e setters (também obrigatórios para o Jackson)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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