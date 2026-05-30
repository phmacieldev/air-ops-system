package com.air_ops_system.certifications.controller;

import com.air_ops_system.certifications.dto.CertificationCreateDTO;
import com.air_ops_system.certifications.dto.CertificationResponseDTO;
import com.air_ops_system.certifications.service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/certifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CertificationController {

  private final CertificationService certificationService;

  @GetMapping
  public ResponseEntity<List<CertificationResponseDTO>> getAll() {
    return ResponseEntity.ok(certificationService.getAll());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('INSTRUCTOR', 'SUPERVISOR', 'LEAD', 'ADM')")
  public ResponseEntity<CertificationResponseDTO> create(@RequestBody @Valid CertificationCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(certificationService.create(dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    certificationService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
