package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.Organization;

public class OrganizationResponse {

    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String address;

    private Long adminId;
    private String adminEmail;

    public OrganizationResponse() {
    }

    public OrganizationResponse(Long id,
                                String name,
                                String description,
                                String email,
                                String phone,
                                String address,
                                Long adminId,
                                String adminEmail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.adminId = adminId;
        this.adminEmail = adminEmail;
    }

    public static OrganizationResponse fromDomain(Organization org) {
        Long adminId = null;
        String adminEmail = null;

        if (org.getAdmin() != null) {
            adminId = org.getAdmin().getId();
            adminEmail = org.getAdmin().getEmail();
        }

        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getDescription(),
                org.getEmail(),
                org.getPhone(),
                org.getAddress(),
                adminId,
                adminEmail
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public Long getAdminId() {
        return adminId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}