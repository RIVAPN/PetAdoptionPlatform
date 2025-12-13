package com.petadoption.backend.core.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "adoption_applications")
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pet para o qual a pessoa/ONG está se candidatando
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // Adotante pode ser um usuário OU uma organização
    @ManyToOne
    @JoinColumn(name = "adopter_user_id")
    private User adopterUser;

    @ManyToOne
    @JoinColumn(name = "adopter_org_id")
    private Organization adopterOrg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private Integer compatibilityScore;
    private Boolean hasBlockingFactor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "decision_at")
    private OffsetDateTime decisionAt;

    @ManyToOne
    @JoinColumn(name = "decision_by_user_id")
    private User decisionByUser;

    private String rejectionReason;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    // ===== getters/setters =====

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public User getAdopterUser() {
        return adopterUser;
    }

    public void setAdopterUser(User adopterUser) {
        this.adopterUser = adopterUser;
    }

    public Organization getAdopterOrg() {
        return adopterOrg;
    }

    public void setAdopterOrg(Organization adopterOrg) {
        this.adopterOrg = adopterOrg;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Integer getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(Integer compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public Boolean getHasBlockingFactor() {
        return hasBlockingFactor;
    }

    public void setHasBlockingFactor(Boolean hasBlockingFactor) {
        this.hasBlockingFactor = hasBlockingFactor;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getDecisionAt() {
        return decisionAt;
    }

    public void setDecisionAt(OffsetDateTime decisionAt) {
        this.decisionAt = decisionAt;
    }

    public User getDecisionByUser() {
        return decisionByUser;
    }

    public void setDecisionByUser(User decisionByUser) {
        this.decisionByUser = decisionByUser;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}