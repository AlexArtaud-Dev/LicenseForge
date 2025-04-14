CREATE TABLE activations
(
    id           UUID                        NOT NULL,
    license_id   UUID                        NOT NULL,
    hardware_id  VARCHAR(255)                NOT NULL,
    activated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_seen_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_activations PRIMARY KEY (id)
);

CREATE TABLE applications
(
    id          UUID                        NOT NULL,
    name        VARCHAR(255)                NOT NULL,
    description VARCHAR(255),
    realm_id    UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_applications PRIMARY KEY (id)
);

CREATE TABLE companies
(
    id                 UUID                        NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    realm_id           VARCHAR(255)                NOT NULL,
    quota_apps         INTEGER                     NOT NULL,
    quota_keys_per_app INTEGER                     NOT NULL,
    plan_type          VARCHAR(255)                NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (id)
);

CREATE TABLE license_activations
(
    license_id  UUID NOT NULL,
    hardware_id VARCHAR(255)
);

CREATE TABLE licenses
(
    id              UUID                        NOT NULL,
    license_key     VARCHAR(255)                NOT NULL,
    app_id          UUID                        NOT NULL,
    customer_id     VARCHAR(255)                NOT NULL,
    expires_at      TIMESTAMP WITHOUT TIME ZONE,
    max_activations INTEGER                     NOT NULL,
    revoked         BOOLEAN                     NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_licenses PRIMARY KEY (id)
);

CREATE TABLE realms
(
    id          UUID                        NOT NULL,
    name        VARCHAR(255)                NOT NULL,
    description VARCHAR(255),
    company_id  UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_realms PRIMARY KEY (id)
);

CREATE TABLE team_permissions
(
    id              UUID                        NOT NULL,
    team_id         UUID                        NOT NULL,
    app_id          UUID                        NOT NULL,
    permission_type VARCHAR(255)                NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_team_permissions PRIMARY KEY (id)
);

CREATE TABLE teams
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    company_id UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            UUID                        NOT NULL,
    email         VARCHAR(255)                NOT NULL,
    first_name    VARCHAR(255)                NOT NULL,
    last_name     VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    role          VARCHAR(255)                NOT NULL,
    company_id    UUID                        NOT NULL,
    team_id       UUID,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE team_permissions
    ADD CONSTRAINT uc_01dc9302a739e1cfe7e7d2d6f UNIQUE (team_id, app_id);

ALTER TABLE activations
    ADD CONSTRAINT uc_9332dd0547ce147ab24d1aaf4 UNIQUE (license_id);

ALTER TABLE companies
    ADD CONSTRAINT uc_companies_realmid UNIQUE (realm_id);

ALTER TABLE licenses
    ADD CONSTRAINT uc_licenses_licensekey UNIQUE (license_key);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE license_activations
    ADD CONSTRAINT fk_license_activations_on_license FOREIGN KEY (license_id) REFERENCES licenses (id);