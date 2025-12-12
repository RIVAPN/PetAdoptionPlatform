package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.PetStatus;

public class CreatePetRequest {

    private String name;
    private String species;
    private String breed;
    private String sex;
    private String size;
    private Integer ageYears;
    private PetStatus status;      // enviar "AVAILABLE", "ADOPTED", etc.

    private Long ownerUserId;      // exatamente UM dos dois deve estar preenchido
    private Long ownerOrgId;

    private Boolean hasSpecialNeeds;
    private Boolean hasContinuousTreatment;
    private Boolean hasChronicDisease;
    private String healthNotes;
    private Boolean goodWithOtherAnimals;
    private Boolean requiresConstantCare;

    public CreatePetRequest() {
    }

    // getters e setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(Integer ageYears) {
        this.ageYears = ageYears;
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getOwnerOrgId() {
        return ownerOrgId;
    }

    public void setOwnerOrgId(Long ownerOrgId) {
        this.ownerOrgId = ownerOrgId;
    }

    public Boolean getHasSpecialNeeds() {
        return hasSpecialNeeds;
    }

    public void setHasSpecialNeeds(Boolean hasSpecialNeeds) {
        this.hasSpecialNeeds = hasSpecialNeeds;
    }

    public Boolean getHasContinuousTreatment() {
        return hasContinuousTreatment;
    }

    public void setHasContinuousTreatment(Boolean hasContinuousTreatment) {
        this.hasContinuousTreatment = hasContinuousTreatment;
    }

    public Boolean getHasChronicDisease() {
        return hasChronicDisease;
    }

    public void setHasChronicDisease(Boolean hasChronicDisease) {
        this.hasChronicDisease = hasChronicDisease;
    }

    public String getHealthNotes() {
        return healthNotes;
    }

    public void setHealthNotes(String healthNotes) {
        this.healthNotes = healthNotes;
    }

    public Boolean getGoodWithOtherAnimals() {
        return goodWithOtherAnimals;
    }

    public void setGoodWithOtherAnimals(Boolean goodWithOtherAnimals) {
        this.goodWithOtherAnimals = goodWithOtherAnimals;
    }

    public Boolean getRequiresConstantCare() {
        return requiresConstantCare;
    }

    public void setRequiresConstantCare(Boolean requiresConstantCare) {
        this.requiresConstantCare = requiresConstantCare;
    }
}