package com.air_ops_system.pub.dto;

import java.time.Instant;

public record PublicStatsDTO(
    String periodo,
    int efetivo_ativo,
    int efetivo_total,
    long apreensoes,
    long horas_voo,
    long acidentes,
    int taxa_sucesso,
    Instant atualizado_em
) {}
