package com.air_ops_system.users.controller;

import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.dto.UserLookupDTO;
import com.air_ops_system.users.dto.UserProfileDTO;
import com.air_ops_system.users.repository.UserRepository;
import com.air_ops_system.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final PilotRepository pilotRepository;

  @GetMapping
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/lookup")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public ResponseEntity<UserLookupDTO> lookupByEmail(@RequestParam String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    var pilot = pilotRepository.findByUserEmail(email).orElse(null);
    return ResponseEntity.ok(new UserLookupDTO(
        user.getName(),
        user.getEmail(),
        pilot != null ? pilot.getFullName()       : null,
        pilot != null ? pilot.getCallsign()        : null,
        pilot != null ? pilot.getProfileImageUrl() : null,
        pilot != null ? pilot.getDiscordId()       : null
    ));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id, Authentication authentication) {
    User requester = (User) authentication.getPrincipal();
    userService.deleteUser(id, requester.getEmail());
    return ResponseEntity.noContent().build();
  }
}