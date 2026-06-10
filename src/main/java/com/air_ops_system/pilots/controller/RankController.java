package com.air_ops_system.pilots.controller;

import com.air_ops_system.pilots.dto.RankResponseDTO;
import com.air_ops_system.pilots.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
public class RankController {

  private final RankRepository rankRepository;

  @GetMapping
  public List<RankResponseDTO> getRanks(@RequestParam(required = false) String unit) {
    List<com.air_ops_system.pilots.domain.Rank> ranks = unit != null
        ? rankRepository.findByUnitOrderByHierarchyLevelAsc(unit)
        : rankRepository.findAll().stream()
            .sorted(Comparator.comparingInt(com.air_ops_system.pilots.domain.Rank::getHierarchyLevel))
            .toList();

    return ranks.stream()
        .map(r -> new RankResponseDTO(r.getId(), r.getName(), r.getHierarchyLevel(), r.getDescription(), r.getUnit()))
        .toList();
  }
}
