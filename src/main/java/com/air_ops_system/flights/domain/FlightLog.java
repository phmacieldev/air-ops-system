package com.air_ops_system.flights.domain;

import com.air_ops_system.pilots.domain.Pilot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flight_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightLog {

  @Id
  @Column(nullable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID flightId;

  @ManyToOne
  @JoinColumn(name = "pilot_id")
  Pilot pilot;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  Aircraft aircraft;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  FlightType flightType;

  @Column(nullable = false)
  LocalDateTime startAt;

  @Column(nullable = true)
  LocalDateTime endAt;

  @Column
  String notes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  FlightStatus flightStatus = FlightStatus.PENDING;

  @ManyToOne
  @JoinColumn(name = "approved_by_id")
  Pilot approvedBy;

  @Column(nullable = false)
  LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
