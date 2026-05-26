# AIR OPS SYSTEM — Contexto do Projeto

## O que é

Sistema interno de gestão da unidade aérea **ASD (Air Support Division)** de um servidor FiveM GTA RP com temática
americana (LSPD).

## Stack

- **Backend:** Java 21 + Spring Boot 4.0.6 + Maven
- **Banco:** PostgreSQL (Docker via spring-boot-docker-compose)
- **Auth:** JWT (jjwt 0.13)
- **Segurança:** Spring Security 7.x com `@EnableMethodSecurity` + `@PreAuthorize`
- **Docs:** Swagger / OpenAPI (springdoc)
- **Frontend (próximo):** Next.js 15 + TypeScript + Tailwind + shadcn/ui
- **Deploy futuro:** Vercel (front) + Render/Railway (back) + Neon/Supabase (banco)

## Hierarquia de ranks

| Rank           | hierarchyLevel | Score inicial | Imune a rebaixamento automático |
|----------------|----------------|---------------|---------------------------------|
| LEAD           | 10             | —             | ✅ (nível ≥ 5)                  |
| SUPERVISOR     | 6              | —             | ✅ (nível ≥ 5)                  |
| INSTRUCTOR     | 5              | 1000          | ✅ (nível ≥ 5)                  |
| PILOT_SENIOR   | 4              | 1000          | ❌                              |
| PILOT_PLENO    | 3              | 600           | ❌                              |
| PILOT_STANDARD | 2              | 200           | ❌                              |
| TRAINEE        | 1              | 0             | ❌                              |

> `IMMUNE_LEVEL = 5` — pilotos com `hierarchyLevel >= 5` não são afetados pela promoção/rebaixamento automático por score.

## Permissões por role

- **TRAINEE:** criar protocolo de voo (fica PENDING, precisa aprovação) + criar relatório próprio
- **PILOT_STANDARD / PILOT_PLENO / PILOT_SENIOR:** tudo do Trainee
- **SUPERVISOR:** tudo acima + aprovar/rejeitar relatórios + editar roster
- **LEAD:** acesso total + deletar usuários/pilotos/relatórios/documentos + setar roles manualmente

> Nota: `AuthService.register()` sempre cria como TRAINEE. LEAD seta roles via banco de dados (comportamento intencional).

## Fórmula de pontuação dos relatórios

```
score = seizures × 5 + chases × 3 + operations × 3 − accidents × 5
```

## Progressão de rank por score acumulado (automática)

| Score acumulado | Rank auto-atribuído |
|-----------------|---------------------|
| 0 – 199         | TRAINEE             |
| 200 – 599       | PILOT_STANDARD      |
| 600 – 999       | PILOT_PLENO         |
| 1000+           | PILOT_SENIOR        |

> A progressão só dispara ao aprovar ou deletar um relatório. Pilotos imunes (nível ≥ 5) nunca são afetados.

## Discord — Webhooks

- `DISCORD_WEBHOOK_REPORTS` → canal de relatórios de desempenho
- Embed enviado ao aprovar relatório: piloto, apreensões, perseguições, operações, acidentes, score do relatório, score acumulado

## Estado atual do projeto

### ✅ Parte 1 e 2 — Auth e estrutura base

- Projeto Spring criado e rodando
- PostgreSQL via Docker
- Entidades User, Pilot, Rank
- Auth completo: register, login, JWT, BCrypt
- JwtAuthenticationFilter, SecurityConfig, GlobalExceptionHandler, Swagger

### ✅ Parte 3 — Estrutura de segurança e pilotos

- `User` implementa `UserDetails`
- `PilotService` + `PilotController` (POST /pilots — LEAD/SUPERVISOR)
- `RankSeeder` idempotente com os 7 ranks
- `@EnableMethodSecurity` no SecurityConfig

### ✅ Parte 4 — Módulo de Voos (FlightLog)

- Enums: `Aircraft`, `FlightType`, `FlightStatus`
- `FlightLog` entity + `FlightLogRepository`
- DTOs: `FlightCreateDTO`, `FlightUpdateDTO`, `FlightResponseDTO`, `FlightReviewDTO`
- `FlightService`: create, update, review (approve/reject), getAll, getById, delete
- `FlightController`:
  - `GET /flights`, `GET /flights/{id}` — autenticado
  - `POST /flights` — autenticado
  - `PUT /flights/{id}` — LEAD/SUPERVISOR
  - `POST /flights/{id}/review` — LEAD/SUPERVISOR
  - `DELETE /flights/{id}` — LEAD
- Ao aprovar: calcula duração e soma ao `flightMinutes` do piloto

### ✅ Parte 4.5 — CRUD completo de Users e Pilots

- `UserService` + `UserController`:
  - `GET /users` — LEAD/SUPERVISOR
  - `DELETE /users/{id}` — LEAD (deleta piloto vinculado em cascata)
- `PilotService` + `PilotController` (atualizados):
  - `GET /pilots`, `GET /pilots/{id}` — autenticado
  - `PUT /pilots/{id}` — LEAD/SUPERVISOR (partial update: callsign, rankId, profileImageUrl, status)
  - `DELETE /pilots/{id}` — LEAD
- `PilotResponseDTO` retorna `rankName` (string) em vez de `rankId`
- `Pilot.accumulatedScore` campo persistido (default 0)

### ✅ Parte 5 — Módulo de Relatórios (PerformanceReport)

- `PerformanceReport` entity (`@OneToOne` FlightLog, `@ManyToOne` Pilot/reviewedBy)
- `ReportStatus` enum: `PENDING`, `APPROVED`
- DTOs: `ReportCreateDTO`, `ReportReviewDTO`, `ReportResponseDTO`
- `PerformanceReportRepository`: queries por FlightLog, Pilot, Pilot+Status
- `ReportService`:
  - `createReport`: um relatório por FlightLog (impede duplicata), criado como PENDING
  - `reviewReport`: calcula score, muda para APPROVED, recalcula acumulado, atualiza rank se não-imune, dispara webhook
  - `deleteReport`: se APPROVED → recalcula acumulado → atualiza rank → salva piloto
  - `getAll`, `getByPilot`
- `ReportController`:
  - `GET /reports`, `GET /reports/pilot/{pilotId}`, `POST /reports` — autenticado
  - `POST /reports/{id}/review` — SUPERVISOR, LEAD
  - `DELETE /reports/{id}` — LEAD
- `DiscordWebhookService`: usa `RestClient`, silencioso se URL vazia

### ✅ Parte 6 — Módulo de Documentos

- `Document` entity (id UUID, title, url varchar 512, category varchar 100, createdAt, updatedAt)
- DTOs: `DocumentCreateDTO`, `DocumentUpdateDTO`, `DocumentResponseDTO`
- `DocumentRepository` (JpaRepository)
- `DocumentService`: getAll, create, update (partial), delete
- `DocumentController`:
  - `GET /documents` — autenticado
  - `POST /documents`, `PUT /documents/{id}`, `DELETE /documents/{id}` — LEAD

Documentos cadastrados (dados de produção):
- SOP, Manual Trainee, Avaliação Trainee
- Certificado Pursuit & VCB, Certificado Operational, Certificado Scene Control

### 🔲 Parte 7 — Frontend (Next.js 15)

- Login page
- Dashboard com métricas
- Roster (tabela: callsign, rank, status ativo/inativo)
- Protocolo de voo
- Relatórios com score e barra de progressão de rank
- Painel individual do piloto
- Página de documentos (grid de cards com link para Google Docs)

## Variáveis de ambiente (.env)

```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
JWT_SECRET=air-ops-system-local-dev-secret-1234567890
DISCORD_WEBHOOK_REPORTS=https://discord.com/api/webhooks/...
```

## Como iniciar uma nova sessão no Claude Code

Rode na raiz do projeto:

```bash
claude
```

Primeira mensagem sugerida:
> "Leia o arquivo CONTEXT.md e me ajude a continuar o projeto a partir do estado atual descrito nele."

Referência visual do protótipo: `file:///C:/Users/pedro/Downloads/ASD_portal_prototype.html`
