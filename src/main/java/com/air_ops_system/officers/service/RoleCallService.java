package com.air_ops_system.officers.service;

import com.air_ops_system.officers.domain.*;
import com.air_ops_system.officers.domain.OfficerUnit;
import com.air_ops_system.officers.dto.*;
import com.air_ops_system.officers.repository.OfficerRepository;
import com.air_ops_system.officers.repository.RoleCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleCallService {

  private static final Map<PoliceRank, Integer> RANK_ORDER = Map.ofEntries(
      Map.entry(PoliceRank.CADET,           1),
      Map.entry(PoliceRank.SOLO_CADET,      2),
      Map.entry(PoliceRank.OFFICER,         3),
      Map.entry(PoliceRank.OFFICER_1,       4),
      Map.entry(PoliceRank.OFFICER_2,       5),
      Map.entry(PoliceRank.OFFICER_3,       6),
      Map.entry(PoliceRank.SENIOR_OFFICER,  7),
      Map.entry(PoliceRank.CORPORAL,        8),
      Map.entry(PoliceRank.SERGEANT,        9),
      Map.entry(PoliceRank.LIEUTENANT,      10),
      Map.entry(PoliceRank.CAPTAIN,         11),
      Map.entry(PoliceRank.ASSISTANT_CHIEF, 12),
      Map.entry(PoliceRank.CHIEF,           13),
      Map.entry(PoliceRank.COMMISSIONER,    14)
  );

  private static final int SERGEANT_LEVEL = 9;

  private final RoleCallRepository roleCallRepository;
  private final OfficerRepository officerRepository;

  @Transactional
  public RoleCallRequestDTO create(UUID officerId, CreateRoleCallDTO dto) {
    Officer officer = getOfficer(officerId);

    String currentValue = switch (dto.type()) {
      case PROMOTION -> officer.getRank() != null ? officer.getRank().name() : null;
      case UNIT      -> officer.getUnitMemberships().isEmpty() ? null :
                        officer.getUnitMemberships().stream().map(u -> u.getUnit().name()).sorted().collect(java.util.stream.Collectors.joining(","));
      case BADGE     -> officer.getBadgeNumber() != null ? officer.getBadgeNumber().toString() : null;
      case STATUS    -> officer.getStatus() != null ? officer.getStatus().name() : null;
      case RESIGNATION -> officer.getStatus() != null ? officer.getStatus().name() : null;
      case ABSENCE     -> officer.getStatus() != null ? officer.getStatus().name() : null;
    };

    RoleCallRequest req = RoleCallRequest.builder()
        .officer(officer)
        .type(dto.type())
        .requestedValue(dto.requestedValue())
        .currentValue(currentValue)
        .reason(dto.reason())
        .build();

    return toDTO(roleCallRepository.save(req));
  }

  public List<RoleCallRequestDTO> findPending() {
    return roleCallRepository.findByStatus(RoleCallStatus.PENDING)
        .stream().map(this::toDTO).toList();
  }

  public List<RoleCallRequestDTO> findByOfficer(UUID officerId) {
    return roleCallRepository.findByOfficerIdOrderByCreatedAtDesc(officerId)
        .stream().map(this::toDTO).toList();
  }

  public List<RoleCallRequestDTO> findRecentReviewed() {
    return roleCallRepository.findTop20ByStatusNotOrderByReviewedAtDesc(RoleCallStatus.PENDING)
        .stream().map(this::toDTO).toList();
  }

  public long countPending() {
    return roleCallRepository.countByStatus(RoleCallStatus.PENDING);
  }

  public Map<String, Long> countPendingByUnit() {
    Map<String, Long> result = new java.util.HashMap<>();
    for (Object[] row : roleCallRepository.countPendingGroupedByUnit()) {
      if (row[0] != null) result.put(row[0].toString(), (Long) row[1]);
    }
    return result;
  }

  @Transactional
  public RoleCallRequestDTO review(UUID requestId, ReviewRoleCallDTO dto, String reviewerEmail) {
    RoleCallRequest req = roleCallRepository.findById(requestId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (req.getStatus() != RoleCallStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already reviewed");
    }

    Officer reviewer = officerRepository.findByUserEmail(reviewerEmail)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Reviewer has no officer profile"));

    int reviewerLevel = RANK_ORDER.getOrDefault(reviewer.getRank(), 0);
    if (reviewerLevel < SERGEANT_LEVEL) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Lieutenant+ can review requests");
    }

    req.setStatus(dto.approved() ? RoleCallStatus.APPROVED : RoleCallStatus.REJECTED);
    req.setReviewNote(dto.reviewNote());
    req.setReviewedBy(reviewer.getFullName());
    req.setReviewedAt(LocalDateTime.now());

    if (dto.approved()) {
      applyChange(req.getOfficer(), req.getType(), req.getRequestedValue());
    }

    return toDTO(roleCallRepository.save(req));
  }

  private void applyChange(Officer officer, RoleCallType type, String value) {
    switch (type) {
      case PROMOTION -> officer.setRank(PoliceRank.valueOf(value));
      case UNIT      -> {
        if (value.isBlank()) {
          officer.getUnitMemberships().clear();
        } else {
          PoliceUnit pu = PoliceUnit.valueOf(value);
          boolean alreadyMember = officer.getUnitMemberships().stream()
              .anyMatch(m -> m.getUnit() == pu);
          if (!alreadyMember) {
            officer.getUnitMemberships().add(
                OfficerUnit.builder().officer(officer).unit(pu).build());
          }
        }
      }
      case BADGE       -> officer.setBadgeNumber(Integer.parseInt(value));
      case STATUS      -> officer.setStatus(OfficerStatus.valueOf(value));
      case RESIGNATION -> officer.setStatus(OfficerStatus.INACTIVE);
      case ABSENCE     -> officer.setStatus(OfficerStatus.ABSENT);
    }
    officerRepository.save(officer);
  }

  private Officer getOfficer(UUID id) {
    return officerRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
  }

  private RoleCallRequestDTO toDTO(RoleCallRequest r) {
    return new RoleCallRequestDTO(
        r.getId(),
        r.getOfficer().getId(),
        r.getOfficer().getFullName(),
        r.getOfficer().getCallsign(),
        r.getOfficer().getProfileImageUrl(),
        r.getType(),
        r.getRequestedValue(),
        r.getCurrentValue(),
        r.getStatus(),
        r.getReason(),
        r.getReviewNote(),
        r.getReviewedBy(),
        r.getCreatedAt(),
        r.getReviewedAt()
    );
  }
}
