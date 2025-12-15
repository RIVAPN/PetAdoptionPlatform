package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.PetStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePetRequest {

    @NotBlank(message = "Nome do pet é obrigatório")
    @Size(max = 100, message = "Nome do pet deve ter no máximo 100 caracteres")
    private String name;

    @NotBlank(message = "Espécie é obrigatória (ex: DOG, CAT)")
    @Size(max = 50, message = "Espécie deve ter no máximo 50 caracteres")
    private String species;

    @Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
    private String breed;

    @Size(max = 10, message = "Sexo deve ter no máximo 10 caracteres")
    private String sex;

    @NotBlank(message = "Tamanho é obrigatório (ex: SMALL, MEDIUM, LARGE)")
    @Size(max = 20, message = "Tamanho deve ter no máximo 20 caracteres")
    private String size;

    @NotNull(message = "Idade (anos) é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    private Integer ageYears;

    private PetStatus status;

    private Boolean hasSpecialNeeds;
    private Boolean hasContinuousTreatment;
    private Boolean hasChronicDisease;

    @Size(max = 500, message = "Anotações de saúde devem ter no máximo 500 caracteres")
    private String healthNotes;

    private Boolean goodWithOtherAnimals;
    private Boolean requiresConstantCare;

    // Dono explícito (opcionais, exatamente um ou nenhum – regra tratada no serviço)
    private Long ownerUserId;
    private Long ownerOrgId;

    public CreatePetRequest() {
    }

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
}