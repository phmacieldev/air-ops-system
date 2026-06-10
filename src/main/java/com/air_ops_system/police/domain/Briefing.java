package com.air_ops_system.police.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "police_briefings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Briefing {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false, length = 10)
  private String startTime;

  @Column(nullable = false, length = 10)
  private String endTime;

  @ElementCollection
  @CollectionTable(name = "briefing_announcements", joinColumns = @JoinColumn(name = "briefing_id"))
  @Column(name = "announcement", columnDefinition = "TEXT")
  private List<String> announcements;

  @ElementCollection
  @CollectionTable(name = "briefing_topics", joinColumns = @JoinColumn(name = "briefing_id"))
  @Column(name = "topic", columnDefinition = "TEXT")
  private List<String> topics;

  @ElementCollection
  @CollectionTable(name = "briefing_notices", joinColumns = @JoinColumn(name = "briefing_id"))
  @Column(name = "notice", columnDefinition = "TEXT")
  private List<String> notices;

  @Column(nullable = false, length = 100)
  private String createdBy;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() { createdAt = LocalDateTime.now(); }
}
