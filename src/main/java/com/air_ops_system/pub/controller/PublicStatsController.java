package com.air_ops_system.pub.controller;

import com.air_ops_system.pub.dto.PublicStatsDTO;
import com.air_ops_system.pub.service.PublicStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicStatsController {

  private final PublicStatsService statsService;

  @GetMapping("/stats")
  public PublicStatsDTO getStats() {
    return statsService.getStats();
  }
}
