package com.air_ops_system.flights.controller;

import com.air_ops_system.flights.dto.FlightCreateDTO;
import com.air_ops_system.flights.dto.FlightResponseDTO;
import com.air_ops_system.flights.dto.FlightReviewDTO;
import com.air_ops_system.flights.dto.FlightUpdateDTO;
import com.air_ops_system.flights.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FlightController {
  private final FlightService flightService;

  @GetMapping
  public ResponseEntity<List<FlightResponseDTO>> getAllFlights() {
    return ResponseEntity.ok(flightService.getAllFlights());
  }

  @GetMapping("/{id}")
  public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable UUID id) {
    return ResponseEntity.ok(flightService.getFlightById(id));
  }

  @PostMapping
  public ResponseEntity<FlightResponseDTO> createFlight(@RequestBody @Valid FlightCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable UUID id,
                                                        @RequestBody @Valid FlightUpdateDTO dto) {
    return ResponseEntity.ok(flightService.updateFlight(id, dto));
  }

  @PostMapping("/{id}/review")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<FlightResponseDTO> reviewFlight(@PathVariable UUID id,
                                                        @RequestBody @Valid FlightReviewDTO dto) {
    return ResponseEntity.ok(flightService.reviewFlight(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('LEAD')")
  public ResponseEntity<Void> deleteFlight(@PathVariable UUID id) {
    flightService.deleteFlight(id);
    return ResponseEntity.noContent().build();
  }
}
