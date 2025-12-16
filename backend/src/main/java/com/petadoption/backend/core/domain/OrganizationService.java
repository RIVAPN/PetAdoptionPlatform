package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationMembershipJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateOrganizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationJpaRepository organizationRepository;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final OrganizationMembershipJpaRepository membershipRepository;

    public OrganizationService(OrganizationJpaRepository organizationRepository,
                               UserJpaRepository userRepository,
                               RoleJpaRepository roleRepository,
                               OrganizationMembershipJpaRepository membershipRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
    }

    // =========================
    // CRIAÇÃO
    // =========================
    @Transactional
    public Organization createOrganization(CreateOrganizationRequest request, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));

        Organization org = new Organization();
        org.setName(request.getName());
        org.setDescription(request.getDescription());
        org.setEmail(request.getEmail());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());
        org.setAdmin(admin);

        // salva organização
        Organization saved = organizationRepository.save(org);

        // cria vínculo ADMIN na tabela organization_memberships
        OrganizationMembership membership = new OrganizationMembership();
        membership.setOrganization(saved);
        membership.setUser(admin);
        membership.setRole(OrgMemberRole.ADMIN);
        membershipRepository.save(membership);

        // garante ROLE_ORG_ADMIN para o usuário que criou
        roleRepository.findByName("ROLE_ORG_ADMIN").ifPresent(role -> {
            if (!admin.getRoles().contains(role)) {
                admin.addRole(role);
            }
        });

        return saved;
    }

    // =========================
    // LEITURA
    // =========================
    public Organization getById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));
    }

    public List<Organization> listAll() {
        return organizationRepository.findAll();
    }

    // =========================
    // ATUALIZAÇÃO
    // =========================
    @Transactional
    public Organization updateOrganization(Long orgId,
                                           CreateOrganizationRequest request,
                                           String authenticatedEmail) {

        Organization org = getById(orgId);

        if (!isAdminOfOrganization(org, authenticatedEmail)) {
            throw new IllegalStateException("Você não tem permissão para alterar esta organização.");
        }

        org.setName(request.getName());
        org.setDescription(request.getDescription());
        org.setEmail(request.getEmail());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());

        return organizationRepository.save(org);
    }

    // =========================
    // EXCLUSÃO
    // =========================
    @Transactional
    public void deleteOrganization(Long orgId, String authenticatedEmail) {
        Organization org = getById(orgId);

        if (!isAdminOfOrganization(org, authenticatedEmail)) {
            throw new IllegalStateException("Você não tem permissão para excluir esta organização.");
        }

        organizationRepository.delete(org);
    }

    // =========================
    // MEMBROS (ADMIN / STAFF)
    // =========================

    @Transactional
    public OrganizationMembership addMember(Long orgId,
                                            String userEmailToAdd,
                                            OrgMemberRole role,
                                            String authenticatedEmail) {

        Organization org = getById(orgId);

        if (!isAdminOfOrganization(org, authenticatedEmail)) {
            throw new IllegalStateException("Você não tem permissão para gerenciar membros desta organização.");
        }

        User user = userRepository.findByEmail(userEmailToAdd)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (membershipRepository.existsByOrganizationAndUserAndRole(org, user, role)) {
            throw new IllegalArgumentException("Este usuário já possui esse papel na organização.");
        }

        OrganizationMembership membership = new OrganizationMembership();
        membership.setOrganization(org);
        membership.setUser(user);
        membership.setRole(role);

        OrganizationMembership saved = membershipRepository.save(membership);

        // se for ADMIN, garante ROLE_ORG_ADMIN globalmente
        if (role == OrgMemberRole.ADMIN) {
            roleRepository.findByName("ROLE_ORG_ADMIN").ifPresent(r -> {
                if (!user.getRoles().contains(r)) {
                    user.addRole(r);
                }
            });
        }

        return saved;
    }

    public List<OrganizationMembership> listMembers(Long orgId, String authenticatedEmail) {
        Organization org = getById(orgId);

        if (!isAdminOfOrganization(org, authenticatedEmail)) {
            throw new IllegalStateException("Você não tem permissão para visualizar membros desta organização.");
        }

        return membershipRepository.findByOrganization(org);
    }

    // =========================
    // Helper de permissão
    // =========================
    private boolean isAdminOfOrganization(Organization org, String email) {
        if (org.getAdmin() == null || org.getAdmin().getEmail() == null) {
            return false;
        }
        return org.getAdmin().getEmail().equalsIgnoreCase(email);
    }
}