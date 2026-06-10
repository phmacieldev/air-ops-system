CREATE TABLE IF NOT EXISTS unit_avisos (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    unit        VARCHAR(50)  NOT NULL,
    content     TEXT         NOT NULL,
    created_by  VARCHAR(100) NOT NULL DEFAULT '—',
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_unit_avisos_unit ON unit_avisos(unit);
