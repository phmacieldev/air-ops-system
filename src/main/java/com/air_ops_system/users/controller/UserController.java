package com.air_ops_system.users.controller;

import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.dto.UserProfileDTO;
import com.air_ops_system.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id, Authentication authentication) {
    User requester = (User) authentication.getPrincipal();
    userService.deleteUser(id, requester.getEmail());
    return ResponseEntity.noContent().build();
  }
}