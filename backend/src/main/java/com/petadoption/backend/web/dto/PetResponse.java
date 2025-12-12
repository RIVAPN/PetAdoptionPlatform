package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.PetStatus;

public class PetResponse {

    private Long id;
    private String name;
    private String species;
    private String size;
    private PetStatus status;
    private String ownerType; // "USER" ou "ORG"
    private Long ownerId;

    public PetResponse() {
    }

    public PetResponse(Long id, String name, String species, String size,
                       PetStatus status, String ownerType, Long ownerId) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.size = size;
        this.status = status;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getSize() { return size; }
    public PetStatus getStatus() { return status; }
    public String getOwnerType() { return ownerType; }
    public Long getOwnerId() { return ownerId; }
}