package com.petadoption.backend.infrastructure.persistence.jpa;

import com.petadoption.backend.core.domain.Organization;
import com.petadoption.backend.core.domain.OrganizationMembership;
import com.petadoption.backend.core.domain.OrgMemberRole;
import com.petadoption.backend.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationMembershipJpaRepository
        extends JpaRepository<OrganizationMembership, Long> {

    List<OrganizationMembership> findByOrganization(Organization organization);

    List<OrganizationMembership> findByUser(User user);

    boolean existsByOrganizationAndUserAndRole(Organization organization,
                                               User user,
                                               OrgMemberRole role);
}