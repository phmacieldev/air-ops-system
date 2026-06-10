package com.air_ops_system.police.controller;

import com.air_ops_system.police.domain.Aviso;
import com.air_ops_system.police.domain.Briefing;
import com.air_ops_system.police.domain.Mandado;
import com.air_ops_system.police.dto.*;
import com.air_ops_system.police.repository.AvisoRepository;
import com.air_ops_system.police.repository.BriefingRepository;
import com.air_ops_system.police.repository.MandadoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/police")
@RequiredArgsConstructor
public class PoliceController {

  private final AvisoRepository    avisoRepository;
  private final MandadoRepository  mandadoRepository;
  private final BriefingRepository briefingRepository;

  // ── Avisos ────────────────────────────────────────────────────────────────

  @GetMapping("/avisos")
  @PreAuthorize("isAuthenticated()")
  public List<AvisoDTO> getAvisos() {
    return avisoRepository.findAll().stream()
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .map(a -> new AvisoDTO(a.getId(), a.getContent(), a.getCreatedBy(), a.getCreatedAt()))
        .toList();
  }

  @PostMapping("/avisos")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AvisoDTO> createAviso(@RequestBody @Valid CreateAvisoDTO dto) {
    Aviso saved = avisoRepository.save(
        Aviso.builder().content(dto.content()).createdBy(dto.createdBy() != null ? dto.createdBy() : "—").build()
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AvisoDTO(saved.getId(), saved.getContent(), saved.getCreatedBy(), saved.getCreatedAt()));
  }

  @PutMapping("/avisos/{id}")
  @PreAuthorize("isAuthenticated()")
  public AvisoDTO updateAviso(@PathVariable UUID id, @RequestBody @Valid CreateAvisoDTO dto) {
    Aviso a = avisoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    a.setContent(dto.content());
    avisoRepository.save(a);
    return new AvisoDTO(a.getId(), a.getContent(), a.getCreatedBy(), a.getCreatedAt());
  }

  @DeleteMapping("/avisos/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteAviso(@PathVariable UUID id) {
    avisoRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // ── Mandados ──────────────────────────────────────────────────────────────

  @GetMapping("/mandados")
  @PreAuthorize("isAuthenticated()")
  public List<MandadoDTO> getMandados() {
    return mandadoRepository.findAll().stream()
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .map(m -> new MandadoDTO(m.getId(), m.getSuspectName(), m.getDescription(), m.getStatus(), m.getCreatedBy(), m.getCreatedAt()))
        .toList();
  }

  @PostMapping("/mandados")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MandadoDTO> createMandado(@RequestBody @Valid CreateMandadoDTO dto) {
    Mandado saved = mandadoRepository.save(
        Mandado.builder()
            .suspectName(dto.suspectName())
            .description(dto.description())
            .createdBy(dto.createdBy() != null ? dto.createdBy() : "—")
            .build()
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new MandadoDTO(saved.getId(), saved.getSuspectName(), saved.getDescription(), saved.getStatus(), saved.getCreatedBy(), saved.getCreatedAt()));
  }

  @PatchMapping("/mandados/{id}")
  @PreAuthorize("isAuthenticated()")
  public MandadoDTO patchMandado(@PathVariable UUID id, @RequestBody PatchMandadoDTO dto) {
    Mandado m = mandadoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (dto.status() != null) m.setStatus(dto.status());
    mandadoRepository.save(m);
    return new MandadoDTO(m.getId(), m.getSuspectName(), m.getDescription(), m.getStatus(), m.getCreatedBy(), m.getCreatedAt());
  }

  @DeleteMapping("/mandados/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteMandado(@PathVariable UUID id) {
    mandadoRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // ── Briefings ─────────────────────────────────────────────────────────────

  @GetMapping("/briefings")
  @PreAuthorize("isAuthenticated()")
  public List<BriefingDTO> getBriefings(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size) {
    return briefingRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
        .stream().map(this::toDTO).toList();
  }

  @GetMapping("/briefings/{id}")
  @PreAuthorize("isAuthenticated()")
  public BriefingDTO getBriefing(@PathVariable UUID id) {
    return toDTO(briefingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
  }

  @PostMapping("/briefings")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BriefingDTO> createBriefing(@RequestBody @Valid CreateBriefingDTO dto) {
    Briefing saved = briefingRepository.save(
        Briefing.builder()
            .date(dto.date())
            .startTime(dto.startTime())
            .endTime(dto.endTime())
            .announcements(dto.announcements() != null ? dto.announcements() : List.of())
            .topics(dto.topics() != null ? dto.topics() : List.of())
            .notices(dto.notices() != null ? dto.notices() : List.of())
            .createdBy(dto.createdBy() != null ? dto.createdBy() : "—")
            .build()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
  }

  @PutMapping("/briefings/{id}")
  @PreAuthorize("isAuthenticated()")
  public BriefingDTO updateBriefing(@PathVariable UUID id, @RequestBody @Valid CreateBriefingDTO dto) {
    Briefing b = briefingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    b.setDate(dto.date());
    b.setStartTime(dto.startTime());
    b.setEndTime(dto.endTime());
    b.setAnnouncements(dto.announcements() != null ? dto.announcements() : List.of());
    b.setTopics(dto.topics() != null ? dto.topics() : List.of());
    b.setNotices(dto.notices() != null ? dto.notices() : List.of());
    return toDTO(briefingRepository.save(b));
  }

  @DeleteMapping("/briefings/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteBriefing(@PathVariable UUID id) {
    briefingRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private BriefingDTO toDTO(Briefing b) {
    return new BriefingDTO(b.getId(), b.getDate(), b.getStartTime(), b.getEndTime(),
        b.getAnnouncements(), b.getTopics(), b.getNotices(), b.getCreatedBy(), b.getCreatedAt());
  }
}
