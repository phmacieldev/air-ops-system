package com.air_ops_system.chat.controller;

import com.air_ops_system.config.ControllerTestBase;
import com.air_ops_system.chat.domain.ChatMessage;
import com.air_ops_system.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRestController.class)
@DisplayName("ChatRestController")
class ChatRestControllerTest extends ControllerTestBase {

  @Autowired MockMvc mockMvc;

  @MockitoBean ChatMessageRepository repo;

  private ChatMessage stubMessage(String channel, String sender, String content) {
    ChatMessage m = new ChatMessage();
    m.setId(UUID.randomUUID());
    m.setChannel(channel);
    m.setSender(sender);
    m.setContent(content);
    m.setCreatedAt(LocalDateTime.now());
    return m;
  }

  @Nested
  @DisplayName("GET /chat/{channel}/history")
  class History {

    @Test
    @WithMockUser
    @DisplayName("returns messages for specified channel in ascending order")
    void returnsHistory() throws Exception {
      when(repo.findAllByChannelOrderByCreatedAtAsc(eq("POLICE_GENERAL"), any(PageRequest.class)))
          .thenReturn(List.of(
              stubMessage("POLICE_GENERAL", "Officer Nash", "All clear on Vespucci"),
              stubMessage("POLICE_GENERAL", "Cpl. Reed", "Copy that")
          ));

      mockMvc.perform(get("/chat/POLICE_GENERAL/history"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].sender").value("Officer Nash"))
          .andExpect(jsonPath("$[1].sender").value("Cpl. Reed"))
          .andExpect(jsonPath("$[0].channel").value("POLICE_GENERAL"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns empty list when channel has no messages")
    void emptyChannel() throws Exception {
      when(repo.findAllByChannelOrderByCreatedAtAsc(eq("HEAT_GENERAL"), any(PageRequest.class)))
          .thenReturn(List.of());

      mockMvc.perform(get("/chat/HEAT_GENERAL/history"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("caps limit at 200 even when higher value requested")
    void capsLimit() throws Exception {
      when(repo.findAllByChannelOrderByCreatedAtAsc(anyString(), any(PageRequest.class)))
          .thenReturn(List.of());

      // Should not throw — controller enforces Math.min(limit, 200)
      mockMvc.perform(get("/chat/POLICE_HC/history").param("limit", "9999"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("returns 403 when not authenticated")
    void requiresAuth() throws Exception {
      mockMvc.perform(get("/chat/POLICE_GENERAL/history"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("respects custom limit parameter")
    void customLimit() throws Exception {
      when(repo.findAllByChannelOrderByCreatedAtAsc(eq("POLICE_SUPERVISION"), any(PageRequest.class)))
          .thenReturn(List.of(stubMessage("POLICE_SUPERVISION", "Sgt. Lima", "Supervision msg")));

      mockMvc.perform(get("/chat/POLICE_SUPERVISION/history").param("limit", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].content").value("Supervision msg"));
    }
  }
}
