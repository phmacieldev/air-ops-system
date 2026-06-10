-- Add unit discriminator to ranks table (existing rows are ASD)
ALTER TABLE ranks ADD COLUMN IF NOT EXISTS unit VARCHAR(50) NOT NULL DEFAULT 'ASD';

-- Convert officer_units from composite-PK to surrogate-PK + unique constraint
ALTER TABLE officer_units ADD COLUMN IF NOT EXISTS id UUID;
UPDATE officer_units SET id = gen_random_uuid() WHERE id IS NULL;
ALTER TABLE officer_units ALTER COLUMN id SET NOT NULL;
ALTER TABLE officer_units ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE officer_units DROP CONSTRAINT IF EXISTS officer_units_pkey;
ALTER TABLE officer_units ADD PRIMARY KEY (id);
ALTER TABLE officer_units DROP CONSTRAINT IF EXISTS officer_units_officer_id_unit_key;
ALTER TABLE officer_units ADD CONSTRAINT officer_units_officer_unit_unique UNIQUE (officer_id, unit);

-- Add unit_rank_id FK (nullable — rank is optional per membership)
ALTER TABLE officer_units ADD COLUMN IF NOT EXISTS unit_rank_id UUID REFERENCES ranks(rank_id) ON DELETE SET NULL;
