package com.air_ops_system.officers.service;

import com.air_ops_system.officers.domain.Officer;
import com.air_ops_system.officers.domain.OfficerStatus;
import com.air_ops_system.officers.domain.OfficerUnit;
import com.air_ops_system.officers.domain.PoliceRank;
import com.air_ops_system.officers.domain.PoliceUnit;
import com.air_ops_system.officers.dto.CreateOfficerDTO;
import com.air_ops_system.officers.dto.OfficerResponseDTO;
import com.air_ops_system.officers.dto.OfficerUnitDTO;
import com.air_ops_system.officers.dto.UpdateOfficerDTO;
import com.air_ops_system.officers.repository.OfficerRepository;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.pilots.repository.RankRepository;
import com.air_ops_system.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfficerService")
class OfficerServiceTest {

  @Mock OfficerRepository officerRepository;
  @Mock UserRepository userRepository;
  @Mock PilotRepository pilotRepository;
  @Mock RankRepository rankRepository;

  @InjectMocks OfficerService officerService;

  // ── Helpers ──────────────────────────────────────────────────────────────

  private Officer buildOfficer(UUID id, String fullName, PoliceRank rank) {
    return Officer.builder()
        .id(id)
        .fullName(fullName)
        .callsign("A-01")
        .rank(rank)
        .unitMemberships(new HashSet<>())
        .weapons(new ArrayList<>())
        .employer("Los Santos Police Department")
        .build();
  }

  // ── create ────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("create()")
  class Create {

    @Test
    @DisplayName("persists officer with correct fields")
    void persistsOfficer() {
      UUID id = UUID.randomUUID();
      CreateOfficerDTO dto = new CreateOfficerDTO(
          "Henry Schneider", "H-01", "disc123", null,
          PoliceRank.OFFICER,
          List.of(new CreateOfficerDTO.UnitEntry(PoliceUnit.ASD, null)),
          null);

      Officer saved = buildOfficer(id, "Henry Schneider", PoliceRank.OFFICER);
      when(officerRepository.save(any())).thenReturn(saved);

      OfficerResponseDTO result = officerService.create(dto);

      assertThat(result.fullName()).isEqualTo("Henry Schneider");
      assertThat(result.rank()).isEqualTo(PoliceRank.OFFICER);
      verify(officerRepository, atLeastOnce()).save(any(Officer.class));
    }

    @Test
    @DisplayName("initialises units to empty set when none provided")
    void emptyUnitsWhenNull() {
      CreateOfficerDTO dto = new CreateOfficerDTO(
          "New Cadet", null, null, null, PoliceRank.CADET, null, null);

      Officer saved = buildOfficer(UUID.randomUUID(), "New Cadet", PoliceRank.CADET);
      when(officerRepository.save(any())).thenReturn(saved);

      officerService.create(dto);

      verify(officerRepository).save(argThat(o -> o.getUnitMemberships() != null && o.getUnitMemberships().isEmpty()));
    }

    @Test
    @DisplayName("links user when userEmail matches an existing user")
    void linksUser() {
      var user = new com.air_ops_system.users.domain.User();
      user.setEmail("officer@lspd.com");

      CreateOfficerDTO dto = new CreateOfficerDTO(
          "Linked Officer", null, null, null, PoliceRank.OFFICER, null, "officer@lspd.com");

      when(userRepository.findByEmail("officer@lspd.com")).thenReturn(Optional.of(user));
      Officer saved = buildOfficer(UUID.randomUUID(), "Linked Officer", PoliceRank.OFFICER);
      when(officerRepository.save(any())).thenReturn(saved);

      officerService.create(dto);

      verify(userRepository).findByEmail("officer@lspd.com");
    }
  }

  // ── findAll ───────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findAll()")
  class FindAll {

    @Test
    @DisplayName("returns mapped list of all officers")
    void returnsAll() {
      when(officerRepository.findAll()).thenReturn(List.of(
          buildOfficer(UUID.randomUUID(), "Alpha", PoliceRank.SERGEANT),
          buildOfficer(UUID.randomUUID(), "Bravo", PoliceRank.OFFICER)
      ));

      List<OfficerResponseDTO> result = officerService.findAll();

      assertThat(result).hasSize(2);
      assertThat(result).extracting(OfficerResponseDTO::fullName)
          .containsExactlyInAnyOrder("Alpha", "Bravo");
    }

    @Test
    @DisplayName("returns empty list when no officers exist")
    void emptyList() {
      when(officerRepository.findAll()).thenReturn(List.of());

      assertThat(officerService.findAll()).isEmpty();
    }
  }

  // ── findById ──────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findById()")
  class FindById {

    @Test
    @DisplayName("throws 404 when officer does not exist")
    void throwsNotFound() {
      UUID id = UUID.randomUUID();
      when(officerRepository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> officerService.findById(id))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404");
    }

    @Test
    @DisplayName("returns officer DTO when found")
    void returnsOfficer() {
      UUID id = UUID.randomUUID();
      when(officerRepository.findById(id))
          .thenReturn(Optional.of(buildOfficer(id, "Found", PoliceRank.CORPORAL)));

      OfficerResponseDTO result = officerService.findById(id);

      assertThat(result.id()).isEqualTo(id);
      assertThat(result.fullName()).isEqualTo("Found");
    }
  }

  // ── patch ─────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("patch()")
  class Patch {

    @Test
    @DisplayName("updates only provided fields (partial update)")
    void partialUpdate() {
      UUID id = UUID.randomUUID();
      Officer officer = buildOfficer(id, "Original", PoliceRank.OFFICER);
      when(officerRepository.findById(id)).thenReturn(Optional.of(officer));
      when(officerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      UpdateOfficerDTO dto = new UpdateOfficerDTO(
          "Updated Name", null, null, PoliceRank.OFFICER_1,
          null, null, null, null, null, null, null, null, null);

      OfficerResponseDTO result = officerService.patch(id, dto);

      assertThat(result.fullName()).isEqualTo("Updated Name");
      assertThat(result.rank()).isEqualTo(PoliceRank.OFFICER_1);
    }

    @Test
    @DisplayName("throws CONFLICT when badge number is already taken by another officer")
    void badgeConflict() {
      UUID id = UUID.randomUUID();
      Officer officer = buildOfficer(id, "Officer", PoliceRank.OFFICER);
      officer.setBadgeNumber(1001);
      when(officerRepository.findById(id)).thenReturn(Optional.of(officer));
      when(officerRepository.existsByBadgeNumber(2001)).thenReturn(true);

      UpdateOfficerDTO dto = new UpdateOfficerDTO(
          null, null, null, null, null, null, 2001,
          null, null, null, null, null, null);

      assertThatThrownBy(() -> officerService.patch(id, dto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("409");
    }

    @Test
    @DisplayName("allows reassigning same badge number to the same officer")
    void allowsSameBadge() {
      UUID id = UUID.randomUUID();
      Officer officer = buildOfficer(id, "Officer", PoliceRank.OFFICER);
      officer.setBadgeNumber(1001);
      when(officerRepository.findById(id)).thenReturn(Optional.of(officer));
      when(officerRepository.existsByBadgeNumber(1001)).thenReturn(true);
      when(officerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      UpdateOfficerDTO dto = new UpdateOfficerDTO(
          null, null, null, null, null, null, 1001,
          null, null, null, null, null, null);

      assertThatNoException().isThrownBy(() -> officerService.patch(id, dto));
    }

    @Test
    @DisplayName("replaces unit set when units provided")
    void replacesUnits() {
      UUID id = UUID.randomUUID();
      Officer officer = buildOfficer(id, "Multi", PoliceRank.OFFICER);
      when(officerRepository.findById(id)).thenReturn(Optional.of(officer));
      when(officerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      UpdateOfficerDTO dto = new UpdateOfficerDTO(
          null, null, null, null,
          List.of(
              new UpdateOfficerDTO.UnitEntry(PoliceUnit.HEAT, null),
              new UpdateOfficerDTO.UnitEntry(PoliceUnit.MU, null)),
          null, null, null, null, null, null, null, null);

      OfficerResponseDTO result = officerService.patch(id, dto);

      assertThat(result.unitNames()).containsExactlyInAnyOrder("HEAT", "MU");
      assertThat(result.unitNames()).doesNotContain("CID");
    }
  }

  // ── delete ────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("delete()")
  class Delete {

    @Test
    @DisplayName("delegates deletion to repository")
    void deletesOfficer() {
      UUID id = UUID.randomUUID();
      Officer officer = buildOfficer(id, "To Delete", PoliceRank.CADET);
      when(officerRepository.findById(id)).thenReturn(Optional.of(officer));

      officerService.delete(id);

      verify(officerRepository).delete(officer);
    }

    @Test
    @DisplayName("throws 404 before attempting delete when officer not found")
    void throwsBeforeDelete() {
      UUID id = UUID.randomUUID();
      when(officerRepository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> officerService.delete(id))
          .isInstanceOf(ResponseStatusException.class);

      verify(officerRepository, never()).delete(any());
    }
  }
}
