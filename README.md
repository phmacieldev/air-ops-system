# Air Ops System — ASD Backend

API REST para gestão interna da **Air Support Division (ASD)**, unidade aérea do LSPD em servidor FiveM GTA RP.

Controla pilotos, protocolos de voo, relatórios de desempenho, progressão de rank por score e documentos operacionais.

---

## Stack

- Java 21 + Spring Boot 4.0.6
- Spring Security 7 (JWT stateless, `@PreAuthorize`)
- Spring Data JPA + Hibernate
- PostgreSQL — Supabase em produção, Docker local
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

Crie `.env` na raiz (use `.env.example` como base):

```env
POSTGRES_DB=air_ops
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
JWT_SECRET=air-ops-system-local-dev-secret-1234567890
CORS_ALLOWED_ORIGIN=http://localhost:3000
```

### 2. Subir o Banco

```powershell
docker compose up -d
```

### 3. Rodar a API

```powershell
.\mvnw spring-boot:run
```

Spring Boot sobe em `http://localhost:8080` e auto-cria as tabelas via `ddl-auto=update`.

---

## Deploy (Render + Supabase)

### Banco — Supabase

1. Crie um projeto em [supabase.com](https://supabase.com)
2. Vá em **Project Settings → Database → Connection string → JDBC**
3. Copie a URL no formato: `jdbc:postgresql://db.[ref].supabase.co:5432/postgres`
4. Adicione `?sslmode=require` ao final

### Backend — Render

1. Crie um **Web Service** no [Render](https://render.com) apontando para este repositório
2. Selecione **Docker** como ambiente (usa o `Dockerfile` da raiz)
3. Configure as variáveis de ambiente:

| Variável | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://db.[ref].supabase.co:5432/postgres?sslmode=require` |
| `POSTGRES_USER` | `postgres` |
| `POSTGRES_PASSWORD` | senha do banco Supabase |
| `JWT_SECRET` | string aleatória segura (mín. 32 chars) |
| `CORS_ALLOWED_ORIGIN` | URL do frontend na Vercel (ex: `https://asd.vercel.app`) |
| `DISCORD_WEBHOOK_REPORTS` | URL do webhook (opcional) |

4. Faça o deploy — as tabelas são criadas automaticamente no primeiro start.

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

Fluxo: `POST /auth/login` → copiar token → clicar em **Authorize** → colar o JWT → testar rotas protegidas.

---

## Endpoints

### Auth

| Método | Rota            | Acesso           |
|--------|-----------------|------------------|
| POST   | /auth/login     | Público          |
| POST   | /auth/register  | LEAD, SUPERVISOR |
| POST   | /auth/refresh   | Autenticado      |

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

### Ranks

| Método | Rota   | Acesso      |
|--------|--------|-------------|
| GET    | /ranks | Autenticado |

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

| Método | Rota                 | Acesso           |
|--------|----------------------|------------------|
| GET    | /reports             | Autenticado      |
| GET    | /reports/pilot/{id}  | Autenticado      |
| POST   | /reports             | Autenticado      |
| POST   | /reports/{id}/review | LEAD, SUPERVISOR |
| DELETE | /reports/{id}        | LEAD             |

**Fórmula de score:** `apreensões×5 + perseguições×3 + operações×3 − acidentes×5`

**Progressão automática:**

| Score     | Rank           |
|-----------|----------------|
| 0 – 199   | TRAINEE        |
| 200 – 599 | PILOT_STANDARD |
| 600 – 999 | PILOT_PLENO    |
| 1000+     | PILOT_SENIOR   |

> Pilotos com nível ≥ 5 (INSTRUCTOR, SUPERVISOR, LEAD) são imunes à promoção/rebaixamento automático e iniciam com 1000 pts ao serem promovidos.

### Documentos

| Método | Rota            | Acesso      |
|--------|-----------------|-------------|
| GET    | /documents      | Autenticado |
| POST   | /documents      | LEAD        |
| PUT    | /documents/{id} | LEAD        |
| DELETE | /documents/{id} | LEAD        |

---

## Erros Padronizados

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Piloto não encontrado.",
  "path": "/pilots/uuid-aqui",
  "timestamp": "2026-05-27T00:00:00"
}
```

---

## Estrutura de Pacotes

```
com.air_ops_system/
  auth/       — registro, login, refresh, JWT, filtro
  users/      — perfil, listagem, deleção
  pilots/     — CRUD de pilotos, ranks
  flights/    — protocolos de voo
  reports/    — relatórios de desempenho e score
  documents/  — links de documentação operacional
  discord/    — envio de webhooks
  config/     — SecurityConfig, OpenApiConfig, RankSeeder
  common/     — GlobalExceptionHandler, exceptions, DTOs base
```
