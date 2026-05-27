package com.air_ops_system.auth.controller;

import com.air_ops_system.auth.dto.AuthResponseDTO;
import com.air_ops_system.auth.dto.LoginRequestDTO;
import com.air_ops_system.auth.dto.RegisterRequestDTO;
import com.air_ops_system.auth.service.AuthService;
import com.air_ops_system.users.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('LEAD', 'SUPERVISOR')")
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
}
