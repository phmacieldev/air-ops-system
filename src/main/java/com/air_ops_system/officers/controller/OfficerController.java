package com.air_ops_system.officers.controller;

import com.air_ops_system.officers.domain.PoliceUnit;
import com.air_ops_system.officers.dto.CreateOfficerDTO;
import com.air_ops_system.officers.dto.OfficerResponseDTO;
import com.air_ops_system.officers.dto.UpdateOfficerDTO;
import com.air_ops_system.officers.service.OfficerService;
import com.air_ops_system.users.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/officers")
@RequiredArgsConstructor
public class OfficerController {

  private final OfficerService officerService;

  @PostMapping
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public ResponseEntity<OfficerResponseDTO> create(@RequestBody @Valid CreateOfficerDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(officerService.create(dto));
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<OfficerResponseDTO>> findAll() {
    return ResponseEntity.ok(officerService.findAll());
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<OfficerResponseDTO> me(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(officerService.findByEmail(user.getEmail()));
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<OfficerResponseDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(officerService.findById(id));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<OfficerResponseDTO> patch(@PathVariable UUID id,
                                                   @RequestBody UpdateOfficerDTO dto) {
    return ResponseEntity.ok(officerService.patch(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    officerService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/units/{unit}")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public ResponseEntity<Void> removeFromUnit(@PathVariable UUID id, @PathVariable PoliceUnit unit) {
    officerService.removeFromUnit(id, unit);
    return ResponseEntity.noContent().build();
  }
}
