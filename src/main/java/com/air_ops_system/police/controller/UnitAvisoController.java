package com.air_ops_system.police.controller;

import com.air_ops_system.police.domain.UnitAviso;
import com.air_ops_system.police.dto.CreateUnitAvisoDTO;
import com.air_ops_system.police.dto.UnitAvisoDTO;
import com.air_ops_system.police.dto.UpdateUnitAvisoDTO;
import com.air_ops_system.police.repository.UnitAvisoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unit-avisos")
@RequiredArgsConstructor
public class UnitAvisoController {

  private final UnitAvisoRepository repo;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<UnitAvisoDTO> getByUnit(@RequestParam String unit) {
    return repo.findAllByUnitOrderByCreatedAtDesc(unit).stream()
        .map(this::toDTO).toList();
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UnitAvisoDTO> create(@RequestBody @Valid CreateUnitAvisoDTO dto) {
    UnitAviso saved = repo.save(UnitAviso.builder()
        .unit(dto.unit())
        .content(dto.content())
        .createdBy(dto.createdBy() != null ? dto.createdBy() : "—")
        .build());
    return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public UnitAvisoDTO update(@PathVariable UUID id, @RequestBody @Valid UpdateUnitAvisoDTO dto) {
    UnitAviso a = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    a.setContent(dto.content());
    return toDTO(repo.save(a));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private UnitAvisoDTO toDTO(UnitAviso a) {
    return new UnitAvisoDTO(a.getId(), a.getUnit(), a.getContent(), a.getCreatedBy(), a.getCreatedAt());
  }
}
