package com.air_ops_system.officers.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "officer_weapons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerWeapon {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "officer_id", nullable = false)
  private Officer officer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private WeaponClass weaponClass;

  @Column(nullable = false, length = 100)
  private String serial;
}
