package com.air_ops_system.police.controller;

import com.air_ops_system.config.ControllerTestBase;
import com.air_ops_system.police.domain.Aviso;
import com.air_ops_system.police.domain.Briefing;
import com.air_ops_system.police.domain.Mandado;
import com.air_ops_system.police.domain.MandadoStatus;
import com.air_ops_system.police.repository.AvisoRepository;
import com.air_ops_system.police.repository.BriefingRepository;
import com.air_ops_system.police.repository.MandadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PoliceController.class)
@DisplayName("PoliceController")
class PoliceControllerTest extends ControllerTestBase {

  @Autowired MockMvc mockMvc;

  @MockitoBean AvisoRepository avisoRepository;
  @MockitoBean MandadoRepository mandadoRepository;
  @MockitoBean BriefingRepository briefingRepository;

  // ── Helpers ──────────────────────────────────────────────────────────────

  private Aviso stubAviso(UUID id, String content) {
    Aviso a = new Aviso();
    a.setId(id);
    a.setContent(content);
    a.setCreatedBy("Admin");
    a.setCreatedAt(LocalDateTime.now());
    return a;
  }

  private Mandado stubMandado(UUID id, String suspect) {
    Mandado m = new Mandado();
    m.setId(id);
    m.setSuspectName(suspect);
    m.setDescription("Armed robbery");
    m.setStatus(MandadoStatus.ACTIVE);
    m.setCreatedBy("Officer Jones");
    m.setCreatedAt(LocalDateTime.now());
    return m;
  }

  private Briefing stubBriefing(UUID id) {
    Briefing b = new Briefing();
    b.setId(id);
    b.setDate(LocalDate.now());
    b.setStartTime("08:00");
    b.setEndTime("09:00");
    b.setAnnouncements(List.of("Watch perimeter"));
    b.setTopics(List.of("Patrol routes"));
    b.setNotices(List.of());
    b.setCreatedBy("Lt. Smith");
    b.setCreatedAt(LocalDateTime.now());
    return b;
  }

  // ── Avisos ────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("GET /police/avisos")
  class GetAvisos {

    @Test
    @WithMockUser
    @DisplayName("returns 200 with sorted avisos list")
    void returnsAvisos() throws Exception {
      UUID id = UUID.randomUUID();
      when(avisoRepository.findAll()).thenReturn(List.of(stubAviso(id, "Patrulha extra")));

      mockMvc.perform(get("/police/avisos"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].content").value("Patrulha extra"))
          .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    @DisplayName("returns 403 when not authenticated")
    void requires_authentication() throws Exception {
      mockMvc.perform(get("/police/avisos"))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("POST /police/avisos")
  class CreateAviso {

    @Test
    @WithMockUser
    @DisplayName("creates aviso and returns 201")
    void createsAviso() throws Exception {
      Aviso saved = stubAviso(UUID.randomUUID(), "Operação noturna iniciada");
      when(avisoRepository.save(any())).thenReturn(saved);

      String body = """
          {"content": "Operação noturna iniciada", "createdBy": "Sgt. Lima"}
          """;

      mockMvc.perform(post("/police/avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("Operação noturna iniciada"));
    }

    @Test
    @WithMockUser
    @DisplayName("defaults createdBy to dash when not provided")
    void defaultsCreatedBy() throws Exception {
      Aviso saved = stubAviso(UUID.randomUUID(), "Test");
      when(avisoRepository.save(any())).thenReturn(saved);

      mockMvc.perform(post("/police/avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"Test\"}"))
          .andExpect(status().isCreated());

      verify(avisoRepository).save(argThat(a -> "—".equals(a.getCreatedBy())));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 400 when content is blank")
    void rejectsBlankContent() throws Exception {
      mockMvc.perform(post("/police/avisos")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"\"}"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /police/avisos/{id}")
  class UpdateAviso {

    @Test
    @WithMockUser
    @DisplayName("updates aviso content and returns updated DTO")
    void updatesAviso() throws Exception {
      UUID id = UUID.randomUUID();
      Aviso existing = stubAviso(id, "Old content");
      when(avisoRepository.findById(id)).thenReturn(Optional.of(existing));
      when(avisoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      mockMvc.perform(put("/police/avisos/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"Updated content\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 4xx when aviso does not exist")
    void returnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();
      when(avisoRepository.findById(id)).thenReturn(Optional.empty());

      mockMvc.perform(put("/police/avisos/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"Anything\"}"))
          .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("DELETE /police/avisos/{id}")
  class DeleteAviso {

    @Test
    @WithMockUser
    @DisplayName("deletes aviso and returns 204")
    void deletesAviso() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc.perform(delete("/police/avisos/" + id).with(csrf()))
          .andExpect(status().isNoContent());

      verify(avisoRepository).deleteById(id);
    }
  }

  // ── Mandados ──────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("GET /police/mandados")
  class GetMandados {

    @Test
    @WithMockUser
    @DisplayName("returns list of mandados")
    void returnsMandados() throws Exception {
      when(mandadoRepository.findAll())
          .thenReturn(List.of(stubMandado(UUID.randomUUID(), "John Doe")));

      mockMvc.perform(get("/police/mandados"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].suspectName").value("John Doe"))
          .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
  }

  @Nested
  @DisplayName("POST /police/mandados")
  class CreateMandado {

    @Test
    @WithMockUser
    @DisplayName("creates mandado and returns 201")
    void createsMandado() throws Exception {
      Mandado saved = stubMandado(UUID.randomUUID(), "Jane Doe");
      when(mandadoRepository.save(any())).thenReturn(saved);

      String body = """
          {"suspectName": "Jane Doe", "description": "Theft", "createdBy": "Cpl. Nash"}
          """;

      mockMvc.perform(post("/police/mandados")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.suspectName").value("Jane Doe"));
    }
  }

  @Nested
  @DisplayName("PATCH /police/mandados/{id}")
  class PatchMandado {

    @Test
    @WithMockUser
    @DisplayName("updates mandado status to CLOSED")
    void patchesStatus() throws Exception {
      UUID id = UUID.randomUUID();
      Mandado m = stubMandado(id, "Suspect X");
      when(mandadoRepository.findById(id)).thenReturn(Optional.of(m));
      when(mandadoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      mockMvc.perform(patch("/police/mandados/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"status\": \"EXECUTED\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("EXECUTED"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 4xx when mandado does not exist")
    void notFound() throws Exception {
      UUID id = UUID.randomUUID();
      when(mandadoRepository.findById(id)).thenReturn(Optional.empty());

      mockMvc.perform(patch("/police/mandados/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"status\": \"EXECUTED\"}"))
          .andExpect(status().is4xxClientError());
    }
  }

  // ── Briefings ─────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("GET /police/briefings")
  class GetBriefings {

    @Test
    @WithMockUser
    @DisplayName("returns paginated briefings")
    void returnsBriefings() throws Exception {
      UUID id = UUID.randomUUID();
      when(briefingRepository.findAllByOrderByCreatedAtDesc(any(PageRequest.class)))
          .thenReturn(List.of(stubBriefing(id)));

      mockMvc.perform(get("/police/briefings"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].announcements[0]").value("Watch perimeter"));
    }
  }

  @Nested
  @DisplayName("GET /police/briefings/{id}")
  class GetBriefingById {

    @Test
    @WithMockUser
    @DisplayName("returns single briefing by id")
    void returnsBriefing() throws Exception {
      UUID id = UUID.randomUUID();
      when(briefingRepository.findById(id)).thenReturn(Optional.of(stubBriefing(id)));

      mockMvc.perform(get("/police/briefings/" + id))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.createdBy").value("Lt. Smith"));
    }

    @Test
    @WithMockUser
    @DisplayName("returns 4xx for unknown briefing id")
    void notFound() throws Exception {
      UUID id = UUID.randomUUID();
      when(briefingRepository.findById(id)).thenReturn(Optional.empty());

      mockMvc.perform(get("/police/briefings/" + id))
          .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("POST /police/briefings")
  class CreateBriefing {

    @Test
    @WithMockUser
    @DisplayName("creates briefing and returns 201 with full body")
    void createsBriefing() throws Exception {
      UUID id = UUID.randomUUID();
      when(briefingRepository.save(any())).thenReturn(stubBriefing(id));

      String body = """
          {
            "date": "%s",
            "startTime": "08:00",
            "endTime": "09:00",
            "announcements": ["Watch perimeter"],
            "topics": ["Patrol routes"],
            "notices": [],
            "createdBy": "Lt. Smith"
          }
          """.formatted(LocalDate.now());

      mockMvc.perform(post("/police/briefings")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.topics[0]").value("Patrol routes"));
    }
  }

  @Nested
  @DisplayName("PUT /police/briefings/{id}")
  class UpdateBriefing {

    @Test
    @WithMockUser
    @DisplayName("updates all briefing fields")
    void updatesBriefing() throws Exception {
      UUID id = UUID.randomUUID();
      Briefing b = stubBriefing(id);
      when(briefingRepository.findById(id)).thenReturn(Optional.of(b));
      when(briefingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      String body = """
          {
            "date": "%s",
            "startTime": "10:00",
            "endTime": "11:00",
            "announcements": ["New announcement"],
            "topics": [],
            "notices": []
          }
          """.formatted(LocalDate.now());

      mockMvc.perform(put("/police/briefings/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.announcements[0]").value("New announcement"));
    }
  }
}
