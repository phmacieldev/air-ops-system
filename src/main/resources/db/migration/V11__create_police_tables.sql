CREATE TABLE IF NOT EXISTS police_avisos (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content    TEXT NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS police_mandados (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    suspect_name VARCHAR(150) NOT NULL,
    description  TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by   VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS police_briefings (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date       DATE NOT NULL,
    start_time VARCHAR(10) NOT NULL,
    end_time   VARCHAR(10) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS briefing_announcements (
    briefing_id  UUID NOT NULL REFERENCES police_briefings(id) ON DELETE CASCADE,
    announcement TEXT
);

CREATE TABLE IF NOT EXISTS briefing_topics (
    briefing_id UUID NOT NULL REFERENCES police_briefings(id) ON DELETE CASCADE,
    topic       TEXT
);

CREATE TABLE IF NOT EXISTS briefing_notices (
    briefing_id UUID NOT NULL REFERENCES police_briefings(id) ON DELETE CASCADE,
    notice      TEXT
);
