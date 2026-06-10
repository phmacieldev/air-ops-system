package com.air_ops_system.officers.domain;

import com.air_ops_system.pilots.domain.Rank;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "officer_units",
    uniqueConstraints = @UniqueConstraint(columnNames = {"officer_id", "unit"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerUnit {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "officer_id", nullable = false)
  private Officer officer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private PoliceUnit unit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_rank_id")
  private Rank unitRank;
}
