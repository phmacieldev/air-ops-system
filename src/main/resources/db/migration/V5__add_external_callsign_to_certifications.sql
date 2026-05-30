ALTER TABLE certifications ADD COLUMN IF NOT EXISTS external_callsign text;
ALTER TABLE certifications ALTER COLUMN discord_id DROP NOT NULL;
