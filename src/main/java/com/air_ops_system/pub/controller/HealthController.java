package com.air_ops_system.pub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

  private final DataSource dataSource;

  public HealthController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> health() {
    System.out.println("Health check pinged at " + Instant.now());
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", "ok");
    body.put("timestamp", Instant.now().toString());
    body.put("uptime_seconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);

    try (Connection conn = dataSource.getConnection()) {
      conn.createStatement().execute("SELECT 1");
      body.put("database", "connected");
    } catch (Exception e) {
      body.put("database", "error");
      body.put("status", "degraded");
    }

    return ResponseEntity.ok(body);
  }
}
