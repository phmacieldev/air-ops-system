ALTER TABLE flight_log DROP CONSTRAINT IF EXISTS flight_log_flight_type_check;

ALTER TABLE flight_log ADD CONSTRAINT flight_log_flight_type_check
    CHECK (flight_type IN (
        'PATRULHA',
        'BANK_FLEECA_10_90',
        'PURSUIT_10_94',
        'BOOSTING_S',
        'PALETO_BANK',
        'BANK_68_10_90',
        'PATROL'
    ));
