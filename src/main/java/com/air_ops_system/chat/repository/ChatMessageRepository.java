package com.air_ops_system.chat.repository;

import com.air_ops_system.chat.domain.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
  List<ChatMessage> findAllByChannelOrderByCreatedAtAsc(String channel, PageRequest pageable);
}
