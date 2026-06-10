package com.air_ops_system.police.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "police_avisos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Aviso {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false, length = 100)
  private String createdBy;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() { createdAt = LocalDateTime.now(); }
}
