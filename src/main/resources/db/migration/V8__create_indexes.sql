-- flight_log: queries por status (contagem de pendentes, aprovações) e por piloto
CREATE INDEX IF NOT EXISTS idx_flight_log_status ON flight_log(flight_status);
CREATE INDEX IF NOT EXISTS idx_flight_log_pilot  ON flight_log(pilot_id);

-- performance_reports: queries por status e por piloto
CREATE INDEX IF NOT EXISTS idx_reports_status ON performance_reports(status);
CREATE INDEX IF NOT EXISTS idx_reports_pilot  ON performance_reports(pilot_id);

-- pilots: ordenação do roster por score e filtragem por status
CREATE INDEX IF NOT EXISTS idx_pilots_score  ON pilots(accumulated_score DESC);
CREATE INDEX IF NOT EXISTS idx_pilots_status ON pilots(status);

-- certifications: filtragem por tipo e por membro
CREATE INDEX IF NOT EXISTS idx_certs_type   ON certifications(certificate_type);
CREATE INDEX IF NOT EXISTS idx_certs_member ON certifications(member_id);
