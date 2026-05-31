package com.air_ops_system.discord.service;

import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.reports.domain.PerformanceReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DiscordWebhookService {

  @Value("${discord.webhook.reports:}")
  private String reportsWebhookUrl;

  @Value("${discord.webhook.flights:}")
  private String flightsWebhookUrl;

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private final RestClient restClient = RestClient.create();

  public void sendFlightApproved(FlightLog flight) {
    if (flightsWebhookUrl == null || flightsWebhookUrl.isBlank()) return;
    try {
      var pilot = flight.getPilot();
      var fields = List.of(
          field("Piloto",    pilot.getFullName() + " (" + pilot.getCallsign() + ")", true),
          field("Aeronave",  flight.getAircraft().name(),                             true),
          field("Tipo",      flight.getFlightType().name(),                           true),
          field("Início",    flight.getStartAt().format(FMT),                        true),
          field("Fim",       flight.getEndAt() != null ? flight.getEndAt().format(FMT) : "—", true),
          field("Aprovado por", flight.getApprovedBy().getCallsign(),                false)
      );
      send(flightsWebhookUrl, "Protocolo Aprovado — " + pilot.getCallsign(), 4052620, fields, null);
    } catch (Exception ignored) {}
  }

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

    send(reportsWebhookUrl, "Relatório Aprovado — " + pilot.getCallsign(), 4052620, fields, null);
  }

  private void send(String url, String title, int color, List<Map<String, Object>> fields, String footer) {
    var embed = new java.util.HashMap<String, Object>();
    embed.put("title", title);
    embed.put("color", color);
    embed.put("fields", fields);
    if (footer != null) embed.put("footer", Map.of("text", footer));

    restClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("embeds", List.of(embed)))
        .retrieve()
        .toBodilessEntity();
  }

  private Map<String, Object> field(String name, String value, boolean inline) {
    return Map.of("name", name, "value", value, "inline", inline);
  }
}
