package com.petadoption.backend.infrastructure.web.dto;

import com.petadoption.backend.core.domain.OrgMemberRole;
import com.petadoption.backend.core.domain.OrganizationMembership;

public class OrganizationMemberResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private OrgMemberRole role;

    public OrganizationMemberResponse() {
    }

    public OrganizationMemberResponse(Long id,
                                      Long userId,
                                      String userName,
                                      String userEmail,
                                      OrgMemberRole role) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.role = role;
    }

    public static OrganizationMemberResponse fromDomain(OrganizationMembership membership) {
        return new OrganizationMemberResponse(
                membership.getId(),
                membership.getUser().getId(),
                membership.getUser().getName(),
                membership.getUser().getEmail(),
                membership.getRole()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public OrgMemberRole getRole() {
        return role;
    }
}