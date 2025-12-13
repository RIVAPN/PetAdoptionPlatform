-- V2__insert_default_roles.sql
-- Roles padrão do sistema

INSERT INTO roles (name) VALUES
    ('ROLE_ADOPTER'),
    ('ROLE_TUTOR'),
    ('ROLE_ORG_ADMIN'),
    ('ROLE_ORG_STAFF')
ON CONFLICT (name) DO NOTHING;