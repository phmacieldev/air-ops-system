package com.air_ops_system.officers.controller;

import com.air_ops_system.officers.dto.CreateRoleCallDTO;
import com.air_ops_system.officers.dto.ReviewRoleCallDTO;
import com.air_ops_system.officers.dto.RoleCallRequestDTO;
import com.air_ops_system.officers.service.RoleCallService;
import com.air_ops_system.users.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rolecall")
@RequiredArgsConstructor
public class RoleCallController {

  private final RoleCallService roleCallService;

  @PostMapping("/officers/{officerId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<RoleCallRequestDTO> create(
      @PathVariable UUID officerId,
      @RequestBody @Valid CreateRoleCallDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roleCallService.create(officerId, dto));
  }

  @GetMapping("/pending")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<RoleCallRequestDTO>> pending() {
    return ResponseEntity.ok(roleCallService.findPending());
  }

  @GetMapping("/history")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<RoleCallRequestDTO>> history() {
    return ResponseEntity.ok(roleCallService.findRecentReviewed());
  }

  @GetMapping("/pending/count")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Map<String, Long>> pendingCount() {
    return ResponseEntity.ok(Map.of("count", roleCallService.countPending()));
  }

  @GetMapping("/pending/count/by-unit")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Map<String, Long>> pendingCountByUnit() {
    return ResponseEntity.ok(roleCallService.countPendingByUnit());
  }

  @GetMapping("/officers/{officerId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<RoleCallRequestDTO>> byOfficer(@PathVariable UUID officerId) {
    return ResponseEntity.ok(roleCallService.findByOfficer(officerId));
  }

  @PatchMapping("/{requestId}/review")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<RoleCallRequestDTO> review(
      @PathVariable UUID requestId,
      @RequestBody ReviewRoleCallDTO dto,
      Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(roleCallService.review(requestId, dto, user.getEmail()));
  }
}
