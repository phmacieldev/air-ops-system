package com.air_ops_system.certifications.domain;

import com.air_ops_system.pilots.domain.Pilot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, unique = true)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HolderType holderType;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Pilot member;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String discordId;

  @Column
  private String externalRank;

  @Column
  private String externalUnit;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CertificationType certificateType;

  @ManyToOne
  @JoinColumn(name = "issued_by_id", nullable = false)
  private Pilot issuedBy;

  @Column(nullable = false)
  private LocalDateTime issuedAt;

  @Column
  private String notes;

  @PrePersist
  public void prePersist() {
    if (issuedAt == null) {
      issuedAt = LocalDateTime.now();
    }
  }
}
