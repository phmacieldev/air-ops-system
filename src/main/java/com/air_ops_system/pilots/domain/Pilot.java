package com.air_ops_system.pilots.domain;

import com.air_ops_system.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pilots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pilot {

  @Id
  @Column(nullable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String fullName;

  @Column(nullable = false, length = 100)
  private String callsign;

  @Column(nullable = false, length = 100)
  private String discordId;

  @Builder.Default
  @Column(nullable = false)
  private Integer flightMinutes = 0;

  @Builder.Default
  @Column(nullable = false)
  private Integer accumulatedScore = 0;

  @Column(nullable = false)
  private LocalDateTime joinedAt;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PilotStatus status = PilotStatus.ACTIVE;

  @ManyToOne
  @JoinColumn(name = "rank_id")
  private Rank rank;

  @Column(length = 255)
  private String profileImageUrl;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @PrePersist
  public void prePersist() {
    if (joinedAt == null) {
      joinedAt = LocalDateTime.now();
    }

    if (flightMinutes == null) {
      flightMinutes = 0;
    }

    if (status == null) {
      status = PilotStatus.TRAINING;
    }
  }

}
