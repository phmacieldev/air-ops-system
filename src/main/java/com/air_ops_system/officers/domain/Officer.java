package com.air_ops_system.officers.domain;

import com.air_ops_system.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "officers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Officer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String fullName;

  @Column(length = 20)
  private String callsign;

  @Column(length = 255)
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PoliceRank rank;

  @Builder.Default
  @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
  @CollectionTable(name = "officer_units", joinColumns = @JoinColumn(name = "officer_id"))
  @Column(name = "unit")
  @Enumerated(EnumType.STRING)
  private Set<PoliceUnit> units = new HashSet<>();

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OfficerStatus status = OfficerStatus.ACTIVE;

  @Column(unique = true)
  private Integer badgeNumber;

  @Column(length = 50)
  private String phone;

  @Column(length = 100)
  private String dna;

  @Column(length = 100)
  private String fingerprint;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(length = 50)
  private String discordId;

  @Column(nullable = false, length = 100)
  private String employer;

  @Column(nullable = false)
  private LocalDateTime hiredAt;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @Builder.Default
  @OneToMany(mappedBy = "officer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OfficerWeapon> weapons = new ArrayList<>();

  @PrePersist
  public void prePersist() {
    if (hiredAt == null) hiredAt = LocalDateTime.now();
    if (employer == null) employer = "Los Santos Police Department";
    if (status == null) status = OfficerStatus.ACTIVE;
  }
}
