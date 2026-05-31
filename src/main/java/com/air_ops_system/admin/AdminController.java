package com.air_ops_system.admin;

import com.air_ops_system.pilots.dto.PilotResponseDTO;
import com.air_ops_system.pilots.service.PilotService;
import com.air_ops_system.users.domain.Role;
import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEAD', 'ADM')")
public class AdminController {

  private final PilotService pilotService;
  private final UserService userService;

  @GetMapping("/pilots")
  public ResponseEntity<List<PilotResponseDTO>> getAllPilots() {
    return ResponseEntity.ok(pilotService.getAllPilotsAdmin());
  }

  @PatchMapping("/users/{userId}/role")
  public ResponseEntity<Void> changeRole(@PathVariable UUID userId,
                                         @RequestBody Map<String, String> body,
                                         Authentication authentication) {
    User requester = (User) authentication.getPrincipal();
    Role newRole = Role.valueOf(body.get("role"));
    userService.changeRole(userId, newRole, requester.getEmail());
    return ResponseEntity.noContent().build();
  }
}
