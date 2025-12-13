package com.petadoption.backend.core.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    private String breed;
    private String sex;
    private String size;

    @Column(name = "age_years")
    private Integer ageYears;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus status;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @ManyToOne
    @JoinColumn(name = "owner_org_id")
    private Organization ownerOrg;

    private Boolean hasSpecialNeeds;
    private Boolean hasContinuousTreatment;
    private Boolean hasChronicDisease;
    private String healthNotes;
    private Boolean goodWithOtherAnimals;
    private Boolean requiresConstantCare;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // ===== getters/setters =====

    public Long getId() {
        return id;
    }

    // Setter usado principalmente em testes
    public void setId(Long id) {
        this.id = id;
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

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public Organization getOwnerOrg() {
        return ownerOrg;
    }

    public void setOwnerOrg(Organization ownerOrg) {
        this.ownerOrg = ownerOrg;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}