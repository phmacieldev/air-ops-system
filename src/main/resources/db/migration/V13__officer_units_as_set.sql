-- Migrate officer unit from single column to a join table (multi-unit support)
CREATE TABLE IF NOT EXISTS officer_units (
    officer_id UUID        NOT NULL REFERENCES officers(id) ON DELETE CASCADE,
    unit       VARCHAR(50) NOT NULL,
    PRIMARY KEY (officer_id, unit)
);

-- Migrate existing single-unit data only if column still exists
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'officers' AND column_name = 'unit'
  ) THEN
    INSERT INTO officer_units (officer_id, unit)
    SELECT id, unit FROM officers WHERE unit IS NOT NULL
    ON CONFLICT DO NOTHING;

    ALTER TABLE officers DROP COLUMN unit;
  END IF;
END $$;
