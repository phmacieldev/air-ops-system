package com.air_ops_system.users.dto;

public record UserLookupDTO(
    String accountName,
    String email,
    String fullName,
    String callsign,
    String profileImageUrl,
    String discordId
) {}
