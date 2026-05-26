# Air Ops System — ASD Backend

API REST para gestão interna da **Air Support Division (ASD)**, unidade aérea do LSPD em servidor FiveM GTA RP.

Controla pilotos, protocolos de voo, relatórios de desempenho, progressão de rank por score e documentos operacionais.

---

## Stack

- Java 21 + Spring Boot 4.0.6
- Spring Security 7 (JWT stateless, `@PreAuthorize`)
- Spring Data JPA + Hibernate
- PostgreSQL (Docker via `spring-boot-docker-compose`)
- Lombok, Bean Validation
- Springdoc OpenAPI / Swagger

---

## Hierarquia de Ranks

| Rank           | Nível | Imune a rebaixamento automático |
|----------------|-------|---------------------------------|
| LEAD           | 10    | Sim                             |
| SUPERVISOR     | 6     | Sim                             |
| INSTRUCTOR     | 5     | Sim                             |
| PILOT_SENIOR   | 4     | Não                             |
| PILOT_PLENO    | 3     | Não                             |
| PILOT_STANDARD | 2     | Não                             |
| TRAINEE        | 1     | Não                             |

---

## Rodar Localmente

### 1. Variáveis de Ambiente

Crie `.env` na raiz do projeto (use `.env.example` como base):

```env
POSTGRES_DB=air_ops
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
JWT_SECRET=air-ops-system-local-dev-secret-1234567890
DISCORD_WEBHOOK_REPORTS=https://discord.com/api/webhooks/...
```

O `.env` é ignorado pelo Git.

### 2. Subir o Banco

```powershell
docker compose up -d
```

### 3. Rodar a API

```powershell
$env:POSTGRES_PASSWORD='1234'
$env:JWT_SECRET='air-ops-system-local-dev-secret-1234567890'
.\mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

Fluxo: `POST /auth/login` → copiar token → clicar em **Authorize** → colar o JWT → testar rotas protegidas.

---

## Endpoints

### Auth

| Método | Rota           | Acesso  |
|--------|----------------|---------|
| POST   | /auth/register | Público |
| POST   | /auth/login    | Público |

### Usuários

| Método | Rota        | Acesso           |
|--------|-------------|------------------|
| GET    | /me         | Autenticado      |
| GET    | /users      | LEAD, SUPERVISOR |
| DELETE | /users/{id} | LEAD             |

> `DELETE /users/{id}` remove o piloto vinculado em cascata.

### Pilotos

| Método | Rota         | Acesso           |
|--------|--------------|------------------|
| GET    | /pilots      | Autenticado      |
| GET    | /pilots/{id} | Autenticado      |
| POST   | /pilots      | LEAD, SUPERVISOR |
| PUT    | /pilots/{id} | LEAD, SUPERVISOR |
| DELETE | /pilots/{id} | LEAD             |

### Voos (FlightLog)

| Método | Rota                 | Acesso           |
|--------|----------------------|------------------|
| GET    | /flights             | Autenticado      |
| GET    | /flights/{id}        | Autenticado      |
| POST   | /flights             | Autenticado      |
| PUT    | /flights/{id}        | LEAD, SUPERVISOR |
| POST   | /flights/{id}/review | LEAD, SUPERVISOR |
| DELETE | /flights/{id}        | LEAD             |

> Ao aprovar um voo, a duração é calculada e somada ao `flightMinutes` do piloto.

### Relatórios de Desempenho

| Método | Rota                     | Acesso           |
|--------|--------------------------|------------------|
| GET    | /reports                 | Autenticado      |
| GET    | /reports/pilot/{pilotId} | Autenticado      |
| POST   | /reports                 | Autenticado      |
| POST   | /reports/{id}/review     | LEAD, SUPERVISOR |
| DELETE | /reports/{id}            | LEAD             |

**Fórmula de score:** `seizures×5 + chases×3 + operations×3 − accidents×5`

**Progressão automática por score acumulado:**

| Score     | Rank           |
|-----------|----------------|
| 0 – 199   | TRAINEE        |
| 200 – 599 | PILOT_STANDARD |
| 600 – 999 | PILOT_PLENO    |
| 1000+     | PILOT_SENIOR   |

> Pilotos com nível ≥ 5 (INSTRUCTOR, SUPERVISOR, LEAD) são imunes à promoção/rebaixamento automático.
> Ao aprovar um relatório, o webhook do Discord é disparado se `DISCORD_WEBHOOK_REPORTS` estiver configurado.

### Documentos

| Método | Rota            | Acesso      |
|--------|-----------------|-------------|
| GET    | /documents      | Autenticado |
| POST   | /documents      | LEAD        |
| PUT    | /documents/{id} | LEAD        |
| DELETE | /documents/{id} | LEAD        |

> Documentos são links para Google Docs (SOP, manuais, certificados, avaliações).

---

## Erros Padronizados

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Piloto não encontrado.",
  "path": "/pilots/uuid-aqui",
  "timestamp": "2026-05-26T17:00:00"
}
```

---

## Estrutura de Pacotes

```
com.air_ops_system/
  auth/       — registro, login, JWT, filtro
  users/      — perfil, listagem, deleção
  pilots/     — CRUD de pilotos, ranks
  flights/    — protocolos de voo
  reports/    — relatórios de desempenho e score
  documents/  — links de documentação operacional
  discord/    — envio de webhooks
  config/     — SecurityConfig, OpenApiConfig, RankSeeder
  common/     — GlobalExceptionHandler, exceptions, DTOs base
```
