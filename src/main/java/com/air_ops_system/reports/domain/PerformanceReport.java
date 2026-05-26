package com.air_ops_system.reports.domain;

import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.pilots.domain.Pilot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReport {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, unique = true)
  private UUID id;

  // Um relatório pertence a exatamente um voo — FK única garante no máximo 1 por voo
  @OneToOne
  @JoinColumn(name = "flight_log_id", nullable = false, unique = true)
  private FlightLog flightLog;

  @ManyToOne
  @JoinColumn(name = "pilot_id", nullable = false)
  private Pilot pilot;

  @Column(nullable = false)
  private int seizures;

  @Column(nullable = false)
  private int chases;

  @Column(nullable = false)
  private int operations;

  @Column(nullable = false)
  private int accidents;

  @Builder.Default
  @Column(nullable = false)
  private int score = 0;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(nullable = false)
  private ReportStatus status = ReportStatus.PENDING;

  @ManyToOne
  @JoinColumn(name = "reviewed_by_id")
  private Pilot reviewedBy;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
