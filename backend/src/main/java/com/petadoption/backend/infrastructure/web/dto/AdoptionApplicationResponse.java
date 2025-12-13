package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.ApplicationStatus;

import java.time.OffsetDateTime;

public class AdoptionApplicationResponse {

    private Long id;
    private Long petId;
    private String adopterType; // USER ou ORG
    private Long adopterId;
    private ApplicationStatus status;
    private Integer compatibilityScore;
    private Boolean hasBlockingFactor;
    private OffsetDateTime createdAt;
    private OffsetDateTime decisionAt;
    private String rejectionReason;

    public AdoptionApplicationResponse() {
    }

    public AdoptionApplicationResponse(Long id, Long petId,
                                       String adopterType, Long adopterId,
                                       ApplicationStatus status,
                                       Integer compatibilityScore,
                                       Boolean hasBlockingFactor,
                                       OffsetDateTime createdAt,
                                       OffsetDateTime decisionAt,
                                       String rejectionReason) {
        this.id = id;
        this.petId = petId;
        this.adopterType = adopterType;
        this.adopterId = adopterId;
        this.status = status;
        this.compatibilityScore = compatibilityScore;
        this.hasBlockingFactor = hasBlockingFactor;
        this.createdAt = createdAt;
        this.decisionAt = decisionAt;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() { return id; }
    public Long getPetId() { return petId; }
    public String getAdopterType() { return adopterType; }
    public Long getAdopterId() { return adopterId; }
    public ApplicationStatus getStatus() { return status; }
    public Integer getCompatibilityScore() { return compatibilityScore; }
    public Boolean getHasBlockingFactor() { return hasBlockingFactor; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getDecisionAt() { return decisionAt; }
    public String getRejectionReason() { return rejectionReason; }
}