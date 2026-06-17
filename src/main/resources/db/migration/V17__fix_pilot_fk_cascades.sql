-- Fix FK constraints that block pilot deletion

DO $$
DECLARE _c TEXT;
BEGIN
  -- certifications.issued_by_id → SET NULL (was RESTRICT + NOT NULL)
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'certifications'::regclass AND c.contype = 'f' AND a.attname = 'issued_by_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE certifications DROP CONSTRAINT %I', _c); END IF;

  -- flight_log.pilot_id → CASCADE (was RESTRICT)
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'flight_log'::regclass AND c.contype = 'f' AND a.attname = 'pilot_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE flight_log DROP CONSTRAINT %I', _c); END IF;

  -- flight_log.approved_by_id → SET NULL (was RESTRICT)
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'flight_log'::regclass AND c.contype = 'f' AND a.attname = 'approved_by_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE flight_log DROP CONSTRAINT %I', _c); END IF;

  -- performance_reports.pilot_id → CASCADE (was RESTRICT)
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'performance_reports'::regclass AND c.contype = 'f' AND a.attname = 'pilot_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE performance_reports DROP CONSTRAINT %I', _c); END IF;

  -- performance_reports.reviewed_by_id → SET NULL (was RESTRICT)
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'performance_reports'::regclass AND c.contype = 'f' AND a.attname = 'reviewed_by_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE performance_reports DROP CONSTRAINT %I', _c); END IF;
END $$;

ALTER TABLE certifications ALTER COLUMN issued_by_id DROP NOT NULL;

ALTER TABLE certifications ADD CONSTRAINT certifications_issued_by_id_fkey
  FOREIGN KEY (issued_by_id) REFERENCES pilots(id) ON DELETE SET NULL;

ALTER TABLE flight_log ADD CONSTRAINT flight_log_pilot_id_fkey
  FOREIGN KEY (pilot_id) REFERENCES pilots(id) ON DELETE CASCADE;

ALTER TABLE flight_log ADD CONSTRAINT flight_log_approved_by_id_fkey
  FOREIGN KEY (approved_by_id) REFERENCES pilots(id) ON DELETE SET NULL;

ALTER TABLE performance_reports ADD CONSTRAINT performance_reports_pilot_id_fkey
  FOREIGN KEY (pilot_id) REFERENCES pilots(id) ON DELETE CASCADE;

ALTER TABLE performance_reports ADD CONSTRAINT performance_reports_reviewed_by_id_fkey
  FOREIGN KEY (reviewed_by_id) REFERENCES pilots(id) ON DELETE SET NULL;
