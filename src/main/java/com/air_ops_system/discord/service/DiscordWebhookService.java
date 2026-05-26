package com.air_ops_system.discord.service;

import com.air_ops_system.reports.domain.PerformanceReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class DiscordWebhookService {

  @Value("${discord.webhook.reports:}")
  private String reportsWebhookUrl;

  private final RestClient restClient = RestClient.create();

  public void sendReportApproved(PerformanceReport report) {
    if (reportsWebhookUrl == null || reportsWebhookUrl.isBlank()) return;

    var pilot = report.getPilot();

    var fields = List.of(
        field("Piloto", pilot.getFullName() + " (" + pilot.getCallsign() + ")", true),
        field("Apreensões", String.valueOf(report.getSeizures()), true),
        field("Perseguições", String.valueOf(report.getChases()), true),
        field("Operações", String.valueOf(report.getOperations()), true),
        field("Acidentes", String.valueOf(report.getAccidents()), true),
        field("Score do relatório", "+" + report.getScore(), false),
        field("Score acumulado", String.valueOf(pilot.getAccumulatedScore()), false)
    );

    var embed = Map.of(
        "title", "Relatório Aprovado — " + pilot.getCallsign(),
        "color", 4052620, // #3dd68c verde
        "fields", fields
    );

    var payload = Map.of("embeds", List.of(embed));

    restClient.post()
        .uri(reportsWebhookUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .retrieve()
        .toBodilessEntity();
  }

  private Map<String, Object> field(String name, String value, boolean inline) {
    return Map.of("name", name, "value", value, "inline", inline);
  }
}
