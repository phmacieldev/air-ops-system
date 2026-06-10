package com.air_ops_system.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageDTO(UUID id, String channel, String sender, String content, LocalDateTime createdAt) {}
