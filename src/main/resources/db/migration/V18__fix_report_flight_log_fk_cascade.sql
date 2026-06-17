-- Fix performance_reports.flight_log_id FK to cascade on flight_log deletion
-- Without this, deleting a pilot with flights+reports fails because
-- flight_log CASCADE runs before performance_reports are removed

DO $$
DECLARE _c TEXT;
BEGIN
  SELECT c.conname INTO _c FROM pg_constraint c
  JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
  WHERE c.conrelid = 'performance_reports'::regclass AND c.contype = 'f' AND a.attname = 'flight_log_id';
  IF _c IS NOT NULL THEN EXECUTE format('ALTER TABLE performance_reports DROP CONSTRAINT %I', _c); END IF;
END $$;

ALTER TABLE performance_reports ADD CONSTRAINT performance_reports_flight_log_id_fkey
  FOREIGN KEY (flight_log_id) REFERENCES flight_log(flight_id) ON DELETE CASCADE;
