package com.air_ops_system.auth.controller;

import com.air_ops_system.auth.dto.AuthResponseDTO;
import com.air_ops_system.auth.dto.ChangeEmailDTO;
import com.air_ops_system.auth.dto.ChangePasswordDTO;
import com.air_ops_system.auth.dto.LoginRequestDTO;
import com.air_ops_system.auth.dto.RegisterRequestDTO;
import com.air_ops_system.auth.service.AuthService;
import com.air_ops_system.users.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM', 'SUPERVISOR')")
  public AuthResponseDTO register(@RequestBody @Valid RegisterRequestDTO dto) {
    return authService.register(dto);
  }

  @PostMapping("/login")
  public AuthResponseDTO login(@RequestBody @Valid LoginRequestDTO dto) {
    return authService.login(dto);
  }

  @PostMapping("/refresh")
  public AuthResponseDTO refresh(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return authService.refresh(user);
  }

  @PatchMapping("/email")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AuthResponseDTO> changeEmail(Authentication authentication,
                                                     @RequestBody @Valid ChangeEmailDTO dto) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(authService.changeEmail(user, dto.currentPassword(), dto.newEmail()));
  }

  @PatchMapping("/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AuthResponseDTO> changePassword(Authentication authentication,
                                                        @RequestBody @Valid ChangePasswordDTO dto) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(authService.changePassword(user, dto.currentPassword(), dto.newPassword()));
  }

  @PostMapping("/setup")
  public ResponseEntity<AuthResponseDTO> setup(@RequestBody @Valid RegisterRequestDTO dto) {
    try {
      return ResponseEntity.ok(authService.setup(dto));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}
