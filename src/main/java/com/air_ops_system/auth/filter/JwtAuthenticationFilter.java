package com.air_ops_system.auth.filter;

import com.air_ops_system.auth.service.TokenService;
import com.air_ops_system.users.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String token = recoverToken(request);

    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      authenticateByToken(token);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticateByToken(String token) {
    try {
      String email = tokenService.getSubject(token);

      userRepository.findByEmail(email).ifPresent(user -> {
        var authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        var authentication = new UsernamePasswordAuthenticationToken(
            user,
            null,
            authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      });
    } catch (JwtException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();
    }
  }

  private String recoverToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return null;
    }

    return authorizationHeader.substring(7);
  }
}
