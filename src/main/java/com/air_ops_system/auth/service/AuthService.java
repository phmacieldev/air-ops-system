package com.air_ops_system.auth.service;

import com.air_ops_system.auth.dto.AuthResponseDTO;
import com.air_ops_system.auth.dto.LoginRequestDTO;
import com.air_ops_system.auth.dto.RegisterRequestDTO;
import com.air_ops_system.common.exception.EmailAlreadyExistsException;
import com.air_ops_system.common.exception.InvalidCredentialsException;
import com.air_ops_system.users.domain.Role;
import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  public AuthResponseDTO register(RegisterRequestDTO dto) {
    if (userRepository.findByEmail(dto.email()).isPresent()) {
      throw new EmailAlreadyExistsException();
    }

    User user = User.builder()
        .name(dto.name())
        .email(dto.email())
        .password(passwordEncoder.encode(dto.password()))
        .role(Role.TRAINEE)
        .build();

    User savedUser = userRepository.save(user);
    String token = tokenService.generateToken(savedUser);

    return new AuthResponseDTO(token);
  }

  public AuthResponseDTO login(LoginRequestDTO dto) {
    User user = userRepository.findByEmail(dto.email())
        .orElseThrow(InvalidCredentialsException::new);

    boolean passwordMatch = passwordEncoder.matches(dto.password(), user.getPassword());

    if (!passwordMatch) {
      throw new InvalidCredentialsException();
    }

    String token = tokenService.generateToken(user);

    return new AuthResponseDTO(token);
  }

  public AuthResponseDTO refresh(User user) {
    return new AuthResponseDTO(tokenService.generateToken(user));
  }

  public AuthResponseDTO setup(RegisterRequestDTO dto) {
    if (userRepository.count() > 0) {
      throw new IllegalStateException("Setup já realizado.");
    }
    if (userRepository.findByEmail(dto.email()).isPresent()) {
      throw new EmailAlreadyExistsException();
    }
    User user = User.builder()
        .name(dto.name())
        .email(dto.email())
        .password(passwordEncoder.encode(dto.password()))
        .role(Role.LEAD)
        .build();
    User saved = userRepository.save(user);
    return new AuthResponseDTO(tokenService.generateToken(saved));
  }
}
