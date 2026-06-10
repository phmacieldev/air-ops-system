package com.air_ops_system.chat.controller;

import com.air_ops_system.chat.domain.ChatMessage;
import com.air_ops_system.chat.dto.ChatMessageDTO;
import com.air_ops_system.chat.dto.IncomingChatDTO;
import com.air_ops_system.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageRepository repo;

  @MessageMapping("/chat/{channel}")
  @SendTo("/topic/chat/{channel}")
  public ChatMessageDTO send(@DestinationVariable String channel, IncomingChatDTO dto) {
    ChatMessage saved = repo.save(ChatMessage.builder()
        .channel(channel)
        .sender(dto.sender() != null ? dto.sender() : "Anônimo")
        .content(dto.content())
        .build());
    return new ChatMessageDTO(saved.getId(), saved.getChannel(), saved.getSender(), saved.getContent(), saved.getCreatedAt());
  }
}
