package com.air_ops_system.pub.controller;

import com.air_ops_system.certifications.dto.CertificationResponseDTO;
import com.air_ops_system.certifications.service.CertificationService;
import com.air_ops_system.pub.dto.PublicStatsDTO;
import com.air_ops_system.pub.service.PublicStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicStatsController {

  private final PublicStatsService statsService;
  private final CertificationService certificationService;

  @GetMapping("/stats")
  public PublicStatsDTO getStats() {
    return statsService.getStats();
  }

  @GetMapping("/certifications")
  public List<CertificationResponseDTO> getCertifications() {
    return certificationService.getAll();
  }
}
