package com.air_ops_system.config;

import com.air_ops_system.pilots.domain.Rank;
import com.air_ops_system.pilots.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component registra essa classe como bean do Spring
// CommandLineRunner = interface com um método run() que o Spring
// chama automaticamente logo após a aplicação subir
@Component
@RequiredArgsConstructor
public class RankSeeder implements CommandLineRunner {

  private final RankRepository rankRepository;

  @Override
  public void run(String... args) {
    seedIfMissing("TRAINEE",        1,  "Piloto em treinamento. Só protocolo de voo com aprovação.");
    seedIfMissing("PILOT_STANDARD", 2,  "Piloto padrão. Protocolo de voo e relatório próprio.");
    seedIfMissing("PILOT_PLENO",    3,  "Piloto pleno. Voos e relatórios independentes.");
    seedIfMissing("PILOT_SENIOR",   4,  "Piloto sênior.");
    seedIfMissing("INSTRUCTOR",     5,  "Instrutor. Avalia trainees e cria relatórios de outros.");
    seedIfMissing("SUPERVISOR",     6,  "Supervisor. Gerencia roster e aprova relatórios.");
    seedIfMissing("LEAD",           10, "Líder. Acesso total, promove e rebaixa qualquer um.");

    // Corrige levels de ranks já existentes no banco (migração)
    updateLevel("PILOT_SENIOR", 4);
    updateLevel("INSTRUCTOR",   5);
    updateLevel("SUPERVISOR",   6);
  }

  private void seedIfMissing(String name, int level, String description) {
    if (rankRepository.findByName(name).isEmpty()) {
      rankRepository.save(Rank.builder().name(name).hierarchyLevel(level).description(description).build());
    }
  }

  private void updateLevel(String name, int newLevel) {
    rankRepository.findByName(name).ifPresent(rank -> {
      if (rank.getHierarchyLevel() != newLevel) {
        rank.setHierarchyLevel(newLevel);
        rankRepository.save(rank);
      }
    });
  }
}