package com.air_ops_system.reports.controller;

import com.air_ops_system.reports.dto.ReportCreateDTO;
import com.air_ops_system.reports.dto.ReportResponseDTO;
import com.air_ops_system.reports.dto.ReportReviewDTO;
import com.air_ops_system.reports.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportController {

  private final ReportService reportService;

  @GetMapping
  public ResponseEntity<List<ReportResponseDTO>> getAllReports() {
    return ResponseEntity.ok(reportService.getAllReports());
  }

  @GetMapping("/pilot/{pilotId}")
  public ResponseEntity<List<ReportResponseDTO>> getReportsByPilot(@PathVariable UUID pilotId) {
    return ResponseEntity.ok(reportService.getReportsByPilot(pilotId));
  }

  @PostMapping
  public ResponseEntity<ReportResponseDTO> createReport(@RequestBody @Valid ReportCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(dto));
  }

  @PostMapping("/{id}/review")
  @PreAuthorize("hasAnyRole('SUPERVISOR', 'LEAD')")
  public ResponseEntity<ReportResponseDTO> reviewReport(@PathVariable UUID id,
                                                        @RequestBody @Valid ReportReviewDTO dto) {
    return ResponseEntity.ok(reportService.reviewReport(id, dto.reviewerEmail()));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
    reportService.deleteReport(id);
    return ResponseEntity.noContent().build();
  }
}
