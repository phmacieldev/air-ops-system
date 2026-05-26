package com.air_ops_system.pilots.controller;

import com.air_ops_system.pilots.dto.CreatePilotDTO;
import com.air_ops_system.pilots.dto.PilotResponseDTO;
import com.air_ops_system.pilots.dto.UpdatePilotDTO;
import com.air_ops_system.pilots.service.PilotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pilots")
@RequiredArgsConstructor
public class PilotController {
  private final PilotService pilotService;

  @PostMapping
  @PreAuthorize("hasAnyRole('LEAD', 'SUPERVISOR')")
  public ResponseEntity<PilotResponseDTO> createPilot(@RequestBody @Valid CreatePilotDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(dto));
  }

  @GetMapping
  public ResponseEntity<List<PilotResponseDTO>> getAllPilots() {
    return ResponseEntity.ok(pilotService.getAllPilots());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PilotResponseDTO> getPilotById(@PathVariable UUID id) {
    return ResponseEntity.ok(pilotService.getPilotById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('LEAD', 'SUPERVISOR')")
  public ResponseEntity<PilotResponseDTO> updatePilot(@PathVariable UUID id, @RequestBody @Valid UpdatePilotDTO dto) {
    return ResponseEntity.ok(pilotService.updatePilot(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<Void> deletePilot(@PathVariable UUID id) {
    pilotService.deletePilot(id);
    return ResponseEntity.noContent().build();
  }
}
