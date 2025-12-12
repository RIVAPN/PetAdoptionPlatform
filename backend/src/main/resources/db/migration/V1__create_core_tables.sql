-- V1__create_core_tables.sql
-- Núcleo do modelo: usuários, roles, organizações, pets, adoções.

-- ===============
-- TABELAS DE USUÁRIO E PERMISSÕES
-- ===============

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    name            TEXT        NOT NULL,
    email           TEXT        NOT NULL UNIQUE,
    password_hash   TEXT        NOT NULL,
    phone           TEXT,
    address         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS roles (
    id      BIGSERIAL PRIMARY KEY,
    name    TEXT NOT NULL UNIQUE  -- ex.: ROLE_ADOPTER, ROLE_TUTOR, ROLE_ORG_ADMIN, ROLE_ORG_STAFF
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ===============
-- ORGANIZAÇÕES E VÍNCULO COM USUÁRIOS
-- ===============

CREATE TABLE IF NOT EXISTS organizations (
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT        NOT NULL,
    description TEXT,
    address     TEXT,
    phone       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS organization_memberships (
    id              BIGSERIAL PRIMARY KEY,
    organization_id BIGINT     NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         BIGINT     NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    org_role        TEXT       NOT NULL,  -- ADMIN ou STAFF
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ===============
-- PERFIL E PREFERÊNCIAS DO ADOTANTE (PESSOA FÍSICA)
-- ===============

CREATE TABLE IF NOT EXISTS adopter_profiles (
    user_id                 BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    housing_type            TEXT,        -- apto, casa, sítio...
    has_children            BOOLEAN,
    number_of_residents     INTEGER,
    has_other_pets          BOOLEAN,
    other_pets_description  TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS adopter_preferences (
    id                          BIGSERIAL PRIMARY KEY,
    adopter_user_id             BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preferred_species           TEXT,
    preferred_size              TEXT,
    preferred_sex               TEXT,
    accepts_special_needs       BOOLEAN,
    accepts_continuous_treatment BOOLEAN,
    accepts_chronic_disease     BOOLEAN,
    wants_low_maintenance       BOOLEAN,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_adopter_preferences_user
    ON adopter_preferences(adopter_user_id);

-- ===============
-- PETS (DONO PODE SER PESSOA OU ORGANIZAÇÃO)
-- ===============

CREATE TABLE IF NOT EXISTS pets (
    id                      BIGSERIAL PRIMARY KEY,
    name                    TEXT        NOT NULL,
    species                 TEXT        NOT NULL,
    breed                   TEXT,
    sex                     TEXT,
    size                    TEXT,
    age_years               INTEGER,
    status                  TEXT        NOT NULL,  -- AVAILABLE, ADOPTED, etc.

    owner_user_id           BIGINT REFERENCES users(id) ON DELETE SET NULL,
    owner_org_id            BIGINT REFERENCES organizations(id) ON DELETE SET NULL,

    has_special_needs       BOOLEAN,
    has_continuous_treatment BOOLEAN,
    has_chronic_disease     BOOLEAN,
    health_notes            TEXT,
    good_with_other_animals BOOLEAN,
    requires_constant_care  BOOLEAN,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (
        (owner_user_id IS NOT NULL AND owner_org_id IS NULL)
        OR (owner_user_id IS NULL AND owner_org_id IS NOT NULL)
    )
);

-- ===============
-- PEDIDOS DE ADOÇÃO
-- (ADOTANTE PODE SER PESSOA OU ORGANIZAÇÃO)
-- ===============

CREATE TABLE IF NOT EXISTS adoption_applications (
    id                      BIGSERIAL PRIMARY KEY,
    pet_id                  BIGINT      NOT NULL REFERENCES pets(id) ON DELETE CASCADE,

    adopter_user_id         BIGINT      REFERENCES users(id) ON DELETE CASCADE,
    adopter_org_id          BIGINT      REFERENCES organizations(id) ON DELETE CASCADE,

    status                  TEXT        NOT NULL, -- PENDING, APPROVED, REJECTED_AUTOMATICALLY, REJECTED_BY_OWNER, ...
    compatibility_score     INTEGER,
    has_blocking_factor     BOOLEAN,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    decision_at             TIMESTAMPTZ,
    decision_by_user_id     BIGINT      REFERENCES users(id),
    rejection_reason        TEXT,

    CHECK (
        (adopter_user_id IS NOT NULL AND adopter_org_id IS NULL)
        OR (adopter_user_id IS NULL AND adopter_org_id IS NOT NULL)
    )
);

-- ===============
-- ADOÇÕES EFETIVADAS (HISTÓRICO, PODE HAVER VÁRIAS POR PET)
-- ===============

CREATE TABLE IF NOT EXISTS adoptions (
    id                      BIGSERIAL PRIMARY KEY,
    application_id          BIGINT      NOT NULL REFERENCES adoption_applications(id) ON DELETE CASCADE,
    pet_id                  BIGINT      NOT NULL REFERENCES pets(id) ON DELETE CASCADE,

    adopter_user_id         BIGINT      REFERENCES users(id) ON DELETE CASCADE,
    adopter_org_id          BIGINT      REFERENCES organizations(id) ON DELETE CASCADE,

    adoption_date           DATE        NOT NULL,
    notes                   TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (
        (adopter_user_id IS NOT NULL AND adopter_org_id IS NULL)
        OR (adopter_user_id IS NULL AND adopter_org_id IS NOT NULL)
    )
);