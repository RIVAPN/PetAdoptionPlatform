-- V3__add_email_and_admin_to_organizations.sql

ALTER TABLE organizations
    ADD COLUMN IF NOT EXISTS email TEXT;

ALTER TABLE organizations
    ADD COLUMN IF NOT EXISTS admin_user_id BIGINT;

ALTER TABLE organizations
    ADD CONSTRAINT fk_organizations_admin_user
        FOREIGN KEY (admin_user_id) REFERENCES users(id);

-- opcional, mas recomendável para performance em filtros
CREATE INDEX IF NOT EXISTS idx_organizations_admin_user_id
    ON organizations(admin_user_id);
