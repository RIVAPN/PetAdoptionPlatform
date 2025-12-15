package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.User;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}