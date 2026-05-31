package com.air_ops_system.admin;

import com.air_ops_system.pilots.dto.PilotResponseDTO;
import com.air_ops_system.pilots.service.PilotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LEAD', 'ADM')")
public class AdminController {

  private final PilotService pilotService;

  @GetMapping("/pilots")
  public ResponseEntity<List<PilotResponseDTO>> getAllPilots() {
    return ResponseEntity.ok(pilotService.getAllPilotsAdmin());
  }
}
