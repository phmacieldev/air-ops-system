package com.air_ops_system.pilots.controller;

import com.air_ops_system.pilots.dto.RankResponseDTO;
import com.air_ops_system.pilots.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
public class RankController {

  private final RankRepository rankRepository;

  @GetMapping
  public List<RankResponseDTO> getAllRanks() {
    return rankRepository.findAll().stream()
        .sorted(Comparator.comparingInt(r -> r.getHierarchyLevel()))
        .map(r -> new RankResponseDTO(r.getId(), r.getName(), r.getHierarchyLevel(), r.getDescription()))
        .toList();
  }
}
