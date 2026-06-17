-- Drop ALL Hibernate-generated duplicate FK constraints on certifications
-- that reference pilots(id), then re-add clean ones.
-- Hibernate may have created duplicates alongside the migration-defined FKs.

DO $$
DECLARE _r RECORD;
BEGIN
  FOR _r IN
    SELECT c.conname FROM pg_constraint c
    JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
    WHERE c.conrelid = 'certifications'::regclass
      AND c.contype = 'f'
      AND a.attname IN ('member_id', 'issued_by_id')
  LOOP
    EXECUTE format('ALTER TABLE certifications DROP CONSTRAINT %I', _r.conname);
  END LOOP;
END $$;

ALTER TABLE certifications ALTER COLUMN issued_by_id DROP NOT NULL;

ALTER TABLE certifications ADD CONSTRAINT certifications_member_id_fkey
  FOREIGN KEY (member_id) REFERENCES pilots(id) ON DELETE SET NULL;

ALTER TABLE certifications ADD CONSTRAINT certifications_issued_by_id_fkey
  FOREIGN KEY (issued_by_id) REFERENCES pilots(id) ON DELETE SET NULL;
