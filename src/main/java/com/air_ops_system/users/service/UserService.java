package com.air_ops_system.users.service;

import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.dto.UserProfileDTO;
import com.air_ops_system.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PilotRepository pilotRepository;

  public List<UserProfileDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(u -> new UserProfileDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt()))
        .toList();
  }

  public void deleteUser(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    // Pilot tem FK para User — precisa deletar o piloto antes do usuário
    pilotRepository.findByUserId(id).ifPresent(pilotRepository::delete);

    userRepository.delete(user);
  }
}