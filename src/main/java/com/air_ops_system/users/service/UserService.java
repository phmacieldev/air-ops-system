package com.air_ops_system.users.service;

import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.users.domain.Role;
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

  public void changeRole(UUID userId, Role newRole, String requesterEmail) {
    User requester = userRepository.findByEmail(requesterEmail)
        .orElseThrow(() -> new RuntimeException("Requisitante não encontrado."));
    User target = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    if (requester.getId().equals(target.getId())) {
      throw new RuntimeException("Não é possível alterar a própria role.");
    }
    if (requester.getRole() == Role.ADM && newRole == Role.ADM) {
      throw new RuntimeException("ADM não pode atribuir role ADM a outro usuário.");
    }

    target.setRole(newRole);
    userRepository.save(target);
  }

  public void deleteUser(UUID id, String requesterEmail) {
    User requester = userRepository.findByEmail(requesterEmail)
        .orElseThrow(() -> new RuntimeException("Requisitante não encontrado."));
    User target = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    if (requester.getId().equals(target.getId())) {
      throw new RuntimeException("Não é possível deletar a própria conta.");
    }

    pilotRepository.findByUserId(id).ifPresent(pilotRepository::delete);
    userRepository.delete(target);
  }
}