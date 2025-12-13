package com.petadoption.backend.infrastructure.web.dto;

public class CreateAdoptionApplicationRequest {

    private Long petId;
    private Long adopterUserId; // um ou outro
    private Long adopterOrgId;

    public CreateAdoptionApplicationRequest() {
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getAdopterUserId() {
        return adopterUserId;
    }

    public void setAdopterUserId(Long adopterUserId) {
        this.adopterUserId = adopterUserId;
    }

    public Long getAdopterOrgId() {
        return adopterOrgId;
    }

    public void setAdopterOrgId(Long adopterOrgId) {
        this.adopterOrgId = adopterOrgId;
    }
}