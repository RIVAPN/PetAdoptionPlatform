package com.petadoption.backend.core.domain;

/**
 * Papel de um usuário dentro de uma organização específica.
 * Isso é diferente dos "roles globais" (Role / ROLE_ADOPTER, ROLE_ORG_ADMIN etc).
 */
public enum OrgMemberRole {
    ADMIN,
    STAFF
}