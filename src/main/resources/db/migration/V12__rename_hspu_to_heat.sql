DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'officers' AND column_name = 'unit'
  ) THEN
    UPDATE officers SET unit = 'HEAT' WHERE unit = 'HSPU';
  END IF;
END $$;
