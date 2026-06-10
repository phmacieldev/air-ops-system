package com.air_ops_system.police.controller;

import com.air_ops_system.config.ControllerTestBase;
import com.air_ops_system.police.domain.UnitAviso;
import com.air_ops_system.police.repository.UnitAvisoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnitAvisoController.class)
@DisplayName("UnitAvisoController")
class UnitAvisoControllerTest extends ControllerTestBase {

  @Autowired MockMvc mockMvc;

  @MockitoBean UnitAvisoRepository repo;

  private UnitAviso stub(UUID id, String unit, String content) {
    UnitAviso a = new UnitAviso();
    a.setId(id);
    a.setUnit(unit);
    a.setContent(content);
    a.setCreatedBy("Lead Officer");
    a.setCreatedAt(LocalDateTime.now());
    return a;
  }

  @Nested
  @DisplayName("GET /unit-avisos?unit=CID")
  class GetByUnit {

    @Test
    @WithMockUser
    @DisplayName("returns avisos filtered by unit")
    void filtersUnit() throws Exception {
      UUID id = UUID.randomUUID();
      when(repo.findAllByUnitOrderByCreatedAtDesc("CID"))
          .thenReturn(List.of(stub(id, "CID", "CID operation tonight")));

      mockMvc.perform(get("/unit-avisos").param("unit", "CID"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].unit").value("CID"))
          .andExpect(jsonPath("$[0].content").value("CID operation tonight"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns empty list when no avisos exist for unit")
    void emptyResult() throws Exception {
      when(repo.findAllByUnitOrderByCreatedAtDesc("HEAT")).thenReturn(List.of());

      mockMvc.perform(get("/unit-avisos").param("unit", "HEAT"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("returns 403 when unauthenticated")
    void requiresAuth() throws Exception {
      mockMvc.perform(get("/unit-avisos").param("unit", "CID"))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("POST /unit-avisos")
  class Create {

    @Test
    @WithMockUser
    @DisplayName("creates aviso for specified unit and returns 201")
    void creates() throws Exception {
      UUID id = UUID.randomUUID();
      when(repo.save(any())).thenReturn(stub(id, "MU", "MU briefing at 20:00"));

      String body = """
          {"unit": "MU", "content": "MU briefing at 20:00", "createdBy": "Lead MU"}
          """;

      mockMvc.perform(post("/unit-avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.unit").value("MU"))
          .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 400 when unit is blank")
    void rejectsBlankUnit() throws Exception {
      mockMvc.perform(post("/unit-avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"unit\": \"\", \"content\": \"Something\"}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("returns 400 when content is blank")
    void rejectsBlankContent() throws Exception {
      mockMvc.perform(post("/unit-avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"unit\": \"CID\", \"content\": \"\"}"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /unit-avisos/{id}")
  class Update {

    @Test
    @WithMockUser
    @DisplayName("updates content and returns updated aviso")
    void updates() throws Exception {
      UUID id = UUID.randomUUID();
      UnitAviso existing = stub(id, "FTO", "Old content");
      when(repo.findById(id)).thenReturn(Optional.of(existing));
      when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

      mockMvc.perform(put("/unit-avisos/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"Updated FTO content\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").value("Updated FTO content"))
          .andExpect(jsonPath("$.unit").value("FTO"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 4xx when aviso not found")
    void notFound() throws Exception {
      UUID id = UUID.randomUUID();
      when(repo.findById(id)).thenReturn(Optional.empty());

      mockMvc.perform(put("/unit-avisos/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"Anything\"}"))
          .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("DELETE /unit-avisos/{id}")
  class Delete {

    @Test
    @WithMockUser
    @DisplayName("deletes and returns 204")
    void deletes() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc.perform(delete("/unit-avisos/" + id).with(csrf()))
          .andExpect(status().isNoContent());

      verify(repo).deleteById(id);
    }
  }
}
