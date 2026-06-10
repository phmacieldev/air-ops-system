package com.air_ops_system.officers.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rolecall_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCallRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "officer_id", nullable = false)
  private Officer officer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleCallType type;

  @Column(nullable = false, length = 100)
  private String requestedValue;

  @Column(length = 100)
  private String currentValue;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleCallStatus status = RoleCallStatus.PENDING;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Column(columnDefinition = "TEXT")
  private String reviewNote;

  @Column(length = 100)
  private String reviewedBy;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime reviewedAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
    status = RoleCallStatus.PENDING;
  }
}
