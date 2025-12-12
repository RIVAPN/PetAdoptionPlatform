package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateOrganizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationJpaRepository organizationRepository;

    public OrganizationService(OrganizationJpaRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Organization createOrganization(CreateOrganizationRequest request) {
        Organization org = new Organization();
        org.setName(request.getName());
        org.setDescription(request.getDescription());
        org.setAddress(request.getAddress());
        org.setPhone(request.getPhone());
        return organizationRepository.save(org);
    }

    public List<Organization> listAll() {
        return organizationRepository.findAll();
    }

    public Organization getById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));
    }
}