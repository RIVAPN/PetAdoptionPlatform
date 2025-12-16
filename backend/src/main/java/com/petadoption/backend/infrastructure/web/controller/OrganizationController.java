package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.OrgMemberRole;
import com.petadoption.backend.core.domain.Organization;
import com.petadoption.backend.core.domain.OrganizationMembership;
import com.petadoption.backend.core.domain.OrganizationService;
import com.petadoption.backend.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // ================
    // ORGANIZAÇÕES
    // ================

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody CreateOrganizationRequest request) {

        String email = getAuthenticatedEmail();
        Organization org = organizationService.createOrganization(request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrganizationResponse.fromDomain(org));
    }

    @GetMapping
    public List<OrganizationResponse> listAll() {
        return organizationService.listAll().stream()
                .map(OrganizationResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public OrganizationResponse getById(@PathVariable Long id) {
        return OrganizationResponse.fromDomain(organizationService.getById(id));
    }

    @PutMapping("/{id}")
    public OrganizationResponse update(@PathVariable Long id,
                                       @Valid @RequestBody CreateOrganizationRequest request) {

        String email = getAuthenticatedEmail();
        Organization updated = organizationService.updateOrganization(id, request, email);
        return OrganizationResponse.fromDomain(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        organizationService.deleteOrganization(id, email);
        return ResponseEntity.noContent().build();
    }

    // ================
    // MEMBROS
    // ================

    @PostMapping("/{id}/members")
    public ResponseEntity<OrganizationMemberResponse> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddOrganizationMemberRequest request) {

        String adminEmail = getAuthenticatedEmail();

        OrgMemberRole role = OrgMemberRole.valueOf(request.getRole().toUpperCase());

        OrganizationMembership membership = organizationService.addMember(
                id,
                request.getUserEmail(),
                role,
                adminEmail
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrganizationMemberResponse.fromDomain(membership));
    }

    @GetMapping("/{id}/members")
    public List<OrganizationMemberResponse> listMembers(@PathVariable Long id) {
        String adminEmail = getAuthenticatedEmail();
        return organizationService.listMembers(id, adminEmail).stream()
                .map(OrganizationMemberResponse::fromDomain)
                .toList();
    }

    // ================
    // Helper
    // ================
    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        // no nosso JWT, o "subject" é o email
        return authentication.getName();
    }
}