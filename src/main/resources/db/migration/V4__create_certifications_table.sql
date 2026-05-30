CREATE TABLE IF NOT EXISTS certifications (
    id               uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    holder_type      text         NOT NULL,
    member_id        uuid         REFERENCES pilots(id) ON DELETE SET NULL,
    full_name        text         NOT NULL,
    discord_id       text         NOT NULL,
    external_rank    text,
    external_unit    text,
    certificate_type text         NOT NULL,
    issued_by_id     uuid         NOT NULL REFERENCES pilots(id),
    issued_at        timestamp    NOT NULL DEFAULT now(),
    notes            text
);
