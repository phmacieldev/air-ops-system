package com.air_ops_system.users.controller;

import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.dto.UserProfileDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProfileController {

  @GetMapping("/me")
  public UserProfileDTO me(Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    return new UserProfileDTO(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt()
    );
  }
}
