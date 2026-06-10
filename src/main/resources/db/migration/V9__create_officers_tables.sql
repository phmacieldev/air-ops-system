CREATE TABLE IF NOT EXISTS officers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name       VARCHAR(100) NOT NULL,
    callsign        VARCHAR(20),
    profile_image_url VARCHAR(255),
    rank            VARCHAR(30) NOT NULL,
    unit            VARCHAR(20),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    badge_number    INTEGER UNIQUE,
    phone           VARCHAR(50),
    dna             VARCHAR(100),
    fingerprint     VARCHAR(100),
    notes           TEXT,
    discord_id      VARCHAR(50),
    employer        VARCHAR(100) NOT NULL DEFAULT 'Los Santos Police Department',
    hired_at        TIMESTAMP NOT NULL DEFAULT now(),
    user_id         UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS officer_weapons (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    officer_id  UUID NOT NULL REFERENCES officers(id) ON DELETE CASCADE,
    weapon_class VARCHAR(20) NOT NULL,
    serial      VARCHAR(100) NOT NULL
);
