package com.air_ops_system.officers.service;

import com.air_ops_system.officers.domain.*;
import com.air_ops_system.officers.dto.*;
import com.air_ops_system.officers.repository.OfficerRepository;
import com.air_ops_system.officers.repository.RoleCallRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleCallService")
class RoleCallServiceTest {

  @Mock RoleCallRepository roleCallRepository;
  @Mock OfficerRepository officerRepository;

  @InjectMocks RoleCallService roleCallService;

  // ── Helpers ──────────────────────────────────────────────────────────────

  private Officer officer(UUID id, PoliceRank rank) {
    return Officer.builder()
        .id(id).fullName("Test Officer").callsign("T-01")
        .rank(rank).units(new HashSet<>()).weapons(new ArrayList<>())
        .employer("LSPD").build();
  }

  private RoleCallRequest pendingRequest(UUID id, Officer officer, RoleCallType type, String value) {
    return RoleCallRequest.builder()
        .id(id).officer(officer).type(type)
        .requestedValue(value).currentValue(null)
        .status(RoleCallStatus.PENDING)
        .build();
  }

  // ── create ────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("create()")
  class Create {

    @Test
    @DisplayName("saves promotion request capturing current rank as currentValue")
    void capturesCurrentRank() {
      UUID officerId = UUID.randomUUID();
      Officer o = officer(officerId, PoliceRank.OFFICER);

      RoleCallRequest saved = pendingRequest(UUID.randomUUID(), o,
          RoleCallType.PROMOTION, "OFFICER_1");
      saved.setCurrentValue("OFFICER");

      when(officerRepository.findById(officerId)).thenReturn(Optional.of(o));
      when(roleCallRepository.save(any())).thenReturn(saved);

      CreateRoleCallDTO dto = new CreateRoleCallDTO(RoleCallType.PROMOTION, "OFFICER_1", "Ready for promotion");
      RoleCallRequestDTO result = roleCallService.create(officerId, dto);

      assertThat(result.currentValue()).isEqualTo("OFFICER");
      assertThat(result.requestedValue()).isEqualTo("OFFICER_1");
      assertThat(result.status()).isEqualTo(RoleCallStatus.PENDING);
    }

    @Test
    @DisplayName("saves badge request capturing current badge as currentValue")
    void capturesCurrentBadge() {
      UUID officerId = UUID.randomUUID();
      Officer o = officer(officerId, PoliceRank.SERGEANT);
      o.setBadgeNumber(501);

      RoleCallRequest saved = pendingRequest(UUID.randomUUID(), o, RoleCallType.BADGE, "502");
      saved.setCurrentValue("501");

      when(officerRepository.findById(officerId)).thenReturn(Optional.of(o));
      when(roleCallRepository.save(any())).thenReturn(saved);

      CreateRoleCallDTO dto = new CreateRoleCallDTO(RoleCallType.BADGE, "502", null);
      RoleCallRequestDTO result = roleCallService.create(officerId, dto);

      assertThat(result.currentValue()).isEqualTo("501");
    }

    @Test
    @DisplayName("throws 404 when officer not found")
    void officerNotFound() {
      UUID officerId = UUID.randomUUID();
      when(officerRepository.findById(officerId)).thenReturn(Optional.empty());

      CreateRoleCallDTO dto = new CreateRoleCallDTO(RoleCallType.PROMOTION, "OFFICER_1", null);

      assertThatThrownBy(() -> roleCallService.create(officerId, dto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404");
    }
  }

  // ── review ────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("review()")
  class Review {

    @Test
    @DisplayName("approves promotion and applies rank change to officer")
    void approvesPromotion() {
      UUID requestId = UUID.randomUUID();
      UUID officerId = UUID.randomUUID();
      UUID reviewerId = UUID.randomUUID();

      Officer applicant = officer(officerId, PoliceRank.OFFICER);
      Officer reviewer  = officer(reviewerId, PoliceRank.LIEUTENANT);
      reviewer.setUser(userWithEmail("lt@lspd.com"));

      RoleCallRequest req = pendingRequest(requestId, applicant, RoleCallType.PROMOTION, "OFFICER_1");

      when(roleCallRepository.findById(requestId)).thenReturn(Optional.of(req));
      when(officerRepository.findByUserEmail("lt@lspd.com")).thenReturn(Optional.of(reviewer));
      when(roleCallRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      ReviewRoleCallDTO reviewDTO = new ReviewRoleCallDTO(true, "Well deserved");
      RoleCallRequestDTO result = roleCallService.review(requestId, reviewDTO, "lt@lspd.com");

      assertThat(result.status()).isEqualTo(RoleCallStatus.APPROVED);
      assertThat(applicant.getRank()).isEqualTo(PoliceRank.OFFICER_1);
      verify(officerRepository).save(applicant);
    }

    @Test
    @DisplayName("rejects request without applying any change")
    void rejectsRequest() {
      UUID requestId = UUID.randomUUID();
      UUID officerId = UUID.randomUUID();
      UUID reviewerId = UUID.randomUUID();

      Officer applicant = officer(officerId, PoliceRank.OFFICER);
      Officer reviewer  = officer(reviewerId, PoliceRank.LIEUTENANT);
      reviewer.setUser(userWithEmail("lt@lspd.com"));

      RoleCallRequest req = pendingRequest(requestId, applicant, RoleCallType.PROMOTION, "OFFICER_1");

      when(roleCallRepository.findById(requestId)).thenReturn(Optional.of(req));
      when(officerRepository.findByUserEmail("lt@lspd.com")).thenReturn(Optional.of(reviewer));
      when(roleCallRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      ReviewRoleCallDTO reviewDTO = new ReviewRoleCallDTO(false, "Not ready yet");
      RoleCallRequestDTO result = roleCallService.review(requestId, reviewDTO, "lt@lspd.com");

      assertThat(result.status()).isEqualTo(RoleCallStatus.REJECTED);
      assertThat(applicant.getRank()).isEqualTo(PoliceRank.OFFICER); // unchanged
    }

    @Test
    @DisplayName("throws FORBIDDEN when reviewer rank is below Lieutenant")
    void lowRankCannotReview() {
      UUID requestId = UUID.randomUUID();
      Officer applicant = officer(UUID.randomUUID(), PoliceRank.CADET);
      Officer reviewer  = officer(UUID.randomUUID(), PoliceRank.SERGEANT); // rank 9, needs 10
      reviewer.setUser(userWithEmail("sgt@lspd.com"));

      RoleCallRequest req = pendingRequest(requestId, applicant, RoleCallType.PROMOTION, "SOLO_CADET");

      when(roleCallRepository.findById(requestId)).thenReturn(Optional.of(req));
      when(officerRepository.findByUserEmail("sgt@lspd.com")).thenReturn(Optional.of(reviewer));

      ReviewRoleCallDTO reviewDTO = new ReviewRoleCallDTO(true, null);

      assertThatThrownBy(() -> roleCallService.review(requestId, reviewDTO, "sgt@lspd.com"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("403");
    }

    @Test
    @DisplayName("throws CONFLICT when request is already reviewed")
    void alreadyReviewed() {
      UUID requestId = UUID.randomUUID();
      Officer applicant = officer(UUID.randomUUID(), PoliceRank.OFFICER);
      RoleCallRequest req = pendingRequest(requestId, applicant, RoleCallType.PROMOTION, "OFFICER_1");
      req.setStatus(RoleCallStatus.APPROVED); // already done

      when(roleCallRepository.findById(requestId)).thenReturn(Optional.of(req));

      ReviewRoleCallDTO reviewDTO = new ReviewRoleCallDTO(true, null);

      assertThatThrownBy(() -> roleCallService.review(requestId, reviewDTO, "any@lspd.com"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("409");
    }

    @Test
    @DisplayName("throws FORBIDDEN when reviewer has no officer profile")
    void reviewerWithoutProfile() {
      UUID requestId = UUID.randomUUID();
      Officer applicant = officer(UUID.randomUUID(), PoliceRank.CADET);
      RoleCallRequest req = pendingRequest(requestId, applicant, RoleCallType.PROMOTION, "SOLO_CADET");

      when(roleCallRepository.findById(requestId)).thenReturn(Optional.of(req));
      when(officerRepository.findByUserEmail("ghost@lspd.com")).thenReturn(Optional.empty());

      ReviewRoleCallDTO reviewDTO = new ReviewRoleCallDTO(true, null);

      assertThatThrownBy(() -> roleCallService.review(requestId, reviewDTO, "ghost@lspd.com"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("403");
    }
  }

  // ── countPending ──────────────────────────────────────────────────────────

  @Nested
  @DisplayName("countPending()")
  class CountPending {

    @Test
    @DisplayName("delegates count to repository")
    void delegatesToRepository() {
      when(roleCallRepository.countByStatus(RoleCallStatus.PENDING)).thenReturn(7L);

      assertThat(roleCallService.countPending()).isEqualTo(7L);
    }
  }

  // ── private helper ────────────────────────────────────────────────────────

  private com.air_ops_system.users.domain.User userWithEmail(String email) {
    var user = new com.air_ops_system.users.domain.User();
    user.setEmail(email);
    return user;
  }
}
