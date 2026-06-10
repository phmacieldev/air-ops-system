package com.air_ops_system.chat.controller;

import com.air_ops_system.chat.dto.ChatMessageDTO;
import com.air_ops_system.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {

  private final ChatMessageRepository repo;

  @GetMapping("/{channel}/history")
  @PreAuthorize("isAuthenticated()")
  public List<ChatMessageDTO> history(
      @PathVariable String channel,
      @RequestParam(defaultValue = "100") int limit) {
    return repo.findAllByChannelOrderByCreatedAtAsc(channel, PageRequest.of(0, Math.min(limit, 200)))
        .stream()
        .map(m -> new ChatMessageDTO(m.getId(), m.getChannel(), m.getSender(), m.getContent(), m.getCreatedAt()))
        .toList();
  }
}
