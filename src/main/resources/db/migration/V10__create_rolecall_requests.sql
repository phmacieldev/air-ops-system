CREATE TABLE IF NOT EXISTS rolecall_requests (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    officer_id       UUID NOT NULL REFERENCES officers(id) ON DELETE CASCADE,
    type             VARCHAR(20) NOT NULL,
    requested_value  VARCHAR(100) NOT NULL,
    current_value    VARCHAR(100),
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason           TEXT,
    review_note      TEXT,
    reviewed_by      VARCHAR(100),
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at      TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rolecall_status ON rolecall_requests(status);
CREATE INDEX IF NOT EXISTS idx_rolecall_officer ON rolecall_requests(officer_id);
