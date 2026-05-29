DO $$
BEGIN
  IF (
    SELECT is_nullable = 'NO'
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name   = 'flight_log'
      AND column_name  = 'end_at'
  ) THEN
    ALTER TABLE flight_log ALTER COLUMN end_at DROP NOT NULL;
  END IF;
END $$;
