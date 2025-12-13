package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.Organization;
import com.petadoption.backend.core.domain.OrganizationService;
import com.petadoption.backend.infrastructure.web.dto.CreateOrganizationRequest;
import com.petadoption.backend.infrastructure.web.dto.OrganizationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@RequestBody CreateOrganizationRequest request) {
        Organization org = organizationService.createOrganization(request);
        OrganizationResponse response = new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getDescription(),
                org.getAddress(),
                org.getPhone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<OrganizationResponse> listAll() {
        return organizationService.listAll().stream()
                .map(org -> new OrganizationResponse(
                        org.getId(),
                        org.getName(),
                        org.getDescription(),
                        org.getAddress(),
                        org.getPhone()
                ))
                .toList();
    }
}