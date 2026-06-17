-- Change certifications.member_id to CASCADE on pilot deletion
-- Member certs should be removed when the pilot is deleted
-- External certs (member_id IS NULL) are not affected

DO $$
DECLARE _r RECORD;
BEGIN
  FOR _r IN
    SELECT c.conname FROM pg_constraint c
    JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
    WHERE c.conrelid = 'certifications'::regclass
      AND c.contype = 'f'
      AND a.attname = 'member_id'
  LOOP
    EXECUTE format('ALTER TABLE certifications DROP CONSTRAINT %I', _r.conname);
  END LOOP;
END $$;

ALTER TABLE certifications ADD CONSTRAINT certifications_member_id_fkey
  FOREIGN KEY (member_id) REFERENCES pilots(id) ON DELETE CASCADE;
