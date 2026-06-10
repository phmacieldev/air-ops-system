package com.air_ops_system.police.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "police_mandados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mandado {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 150)
  private String suspectName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MandadoStatus status = MandadoStatus.ACTIVE;

  @Column(nullable = false, length = 100)
  private String createdBy;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
    if (status == null) status = MandadoStatus.ACTIVE;
  }
}
