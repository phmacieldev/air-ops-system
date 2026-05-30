# ASD Air Ops System

Sistema de gestão operacional da **Air Support Division (ASD)** — unidade aérea de um servidor FiveM GTA RP temática americana (LSPD). Desenvolvido do zero como projeto real de portfólio, utilizando Claude Code como par de programação ao longo de todo o desenvolvimento.

> **Contexto de migração de área:** Este projeto marca minha transição para desenvolvimento de software. Parti de zero conhecimento em Spring Boot e construí uma API REST completa com autenticação JWT, controle de acesso por roles, progressão de rank automática, webhooks Discord, e um frontend Next.js com deploy em produção — tudo documentado e versionado.

---

## Sistema em Produção

| Serviço | URL | Plataforma |
|---------|-----|------------|
| Frontend | https://air-ops-system-web.vercel.app | Vercel (Hobby) |
| Backend API | https://air-ops-system.onrender.com | Render (Free) |
| Banco de dados | Supabase PostgreSQL | Supabase (Free) |

**Keep-alive:** UptimeRobot monitora o backend a cada 5 minutos para evitar o cold start do plano free do Render.

---

## O que o sistema faz

A ASD é uma unidade hierarquizada com pilotos em diferentes ranks. O sistema resolve três problemas reais:

1. **Rastreio de atividade** — cada piloto registra protocolos de voo (missão, aeronave, horário). Aprovados por líderes.
2. **Progressão por mérito** — relatórios de desempenho calculam score (`apreensões×5 + perseguições×3 + ops×3 − acidentes×5`). O rank sobe automaticamente conforme o score acumulado.
3. **Transparência pública** — página `/status` exibe efetivo ativo, horas de voo e taxa de sucesso sem exigir login, atualiza a cada 60 segundos.

---

## Stack

### Backend
| Tecnologia | Versão | Por quê |
|-----------|--------|---------|
| Java | 21 | LTS atual; records, sealed classes, pattern matching |
| Spring Boot | 4.0.6 | Auto-configuração, ecosystem maduro, fácil deploy via JAR |
| Spring Security | 7.x | Stateless JWT com `@PreAuthorize` por método — simples e declarativo |
| Spring Data JPA | — | Repositórios prontos, queries derivadas, sem SQL boilerplate |
| PostgreSQL | 17/18 | Relacional, ACID, suporte nativo no Supabase |
| Flyway | 11.x | Migrações versionadas — schema reproduzível em qualquer ambiente |
| Lombok | — | Elimina boilerplate: `@Builder`, `@Getter`, `@Setter`, `@RequiredArgsConstructor` |
| jjwt | 0.13 | Geração e validação de tokens JWT |
| Springdoc OpenAPI | 2.x | Swagger UI automático a partir das anotações |

### Frontend
| Tecnologia | Versão | Por quê |
|-----------|--------|---------|
| Next.js | 16 | App Router, SSR/SSG, proxy de API integrado |
| TypeScript | 5.x | Type safety, DX superior ao JS puro |
| Tailwind CSS | 4.x | Utilitários direto no JSX, sem CSS files separados |
| shadcn/ui | — | Componentes acessíveis sem overhead de uma biblioteca pesada |

---

## Hierarquia de Ranks e Permissões

```
ADM          → permissões iguais ao LEAD; invisível no roster público
LEAD         → acesso total; aprova voos/relatórios; altera ranks manualmente
SUPERVISOR   → gerencia roster; aprova relatórios
INSTRUCTOR   → emite certificações; avalia trainees
PILOT_SENIOR → voos e relatórios independentes
PILOT_PLENO  → voos e relatórios independentes
PILOT_STANDARD → voos e relatórios independentes
TRAINEE      → protocolo de voo (precisa aprovação)
```

**Progressão automática por score acumulado** (pilotos com hierarchyLevel < 5):

| Score     | Rank           |
|-----------|----------------|
| 0 – 199   | TRAINEE        |
| 200 – 599 | PILOT_STANDARD |
| 600 – 999 | PILOT_PLENO    |
| 1000+     | PILOT_SENIOR   |

INSTRUCTOR, SUPERVISOR e LEAD são imunes — seu rank só muda por ação manual de um LEAD/ADM.

---

## Funcionalidades Implementadas

### Autenticação e Conta
- [x] Registro/login com BCrypt + JWT stateless
- [x] Refresh token automático (interceptor no frontend + `POST /auth/refresh`)
- [x] `POST /auth/setup` — cria o primeiro LEAD; retorna 403 se já existirem usuários (protege o bootstrap)
- [x] `PATCH /auth/email` — troca e-mail com verificação de senha atual
- [x] `PATCH /auth/password` — troca senha com verificação de senha atual

### Pilotos e Ranks
- [x] CRUD completo de pilotos
- [x] Campo `grupo` calculado no backend (`resolveGrupo`): trainee / pilot / instructor / supervisor / lead / adm
- [x] `PATCH /pilots/:id/rank` — LEAD/ADM alteram rank manualmente
- [x] `PATCH /pilots/:id/profile` — piloto edita o próprio callsign e foto (sem tocar em status ou rank)
- [x] Role ADM: permissões de LEAD, mas **filtrada da listagem pública** via `WHERE user.role != 'ADM'` na query — nunca aparece no roster
- [x] Ordenação do roster: score desc → hierarchyLevel desc → callsign asc

### Certificações
- [x] Tipos para membros: `PURSUIT`, `OPERATIONAL`, `SCENE_CONTROL`
- [x] Tipos para externos: `COPILOT`, `TRANSPORT`
- [x] Emissão restrita a Instructor+ ; revogação restrita a LEAD/ADM
- [x] Badges de certificação exibidos nos cards do roster

### Protocolos de Voo
- [x] Criação com validação de data/hora futura (backend + cliente)
- [x] Aprovação/rejeição por LEAD/ADM; calcula `flightMinutes` do piloto na aprovação
- [x] Deleção permitida apenas para status `REJECTED`
- [x] `GET /flights/mine` — retorna só os voos do usuário autenticado

### Relatórios de Desempenho
- [x] Um relatório por voo; vinculado ao FlightLog via `@OneToOne`
- [x] Score calculado na aprovação; acumulado recalculado em cascata
- [x] Progressão automática de rank ao aprovar (exceto ranks imunes)
- [x] Webhook Discord ao aprovar — embed com todos os dados do relatório
- [x] Status `REJECTED` adicionado: rejeitados aparecem com badge vermelho e podem ser deletados por LEAD/ADM
- [x] Deleção de APPROVED recalcula score acumulado e rank do piloto

### Dashboard e Status Público
- [x] Métricas em tempo real: pilotos ativos, horas de voo, apreensões, acidentes
- [x] Ranking de score com desempate por rank e callsign
- [x] Página pública `/status` sem autenticação — polling a cada 60s com countdown visual

### UX e Frontend
- [x] Dark theme militar: `#0a0d12` fundo, `#e8c97e` gold, `#1c2a3a` navy
- [x] Layout responsivo (mobile + desktop) com sidebar colapsável
- [x] Modais de confirmação com variantes `danger` e `warning`
- [x] Contraste WCAG: labels de form em `#c8d6e5`, headers de tabela em `#8a9ab8`
- [x] Proxy Next.js → backend (evita CORS em produção sem expor a URL da API)
- [x] Cache client-side com TTL por endpoint

---

## Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                     Next.js (Vercel)                    │
│  App Router · TypeScript · Tailwind · Client-side JWT   │
│                                                         │
│  /dashboard  /pilots  /flights  /reports                │
│  /certifications  /documents  /settings  /status        │
└────────────────────────┬────────────────────────────────┘
                         │ HTTPS (proxy Next.js)
┌────────────────────────▼────────────────────────────────┐
│              Spring Boot 4 REST API (Render)             │
│                                                         │
│  JwtAuthenticationFilter → SecurityConfig               │
│  Controller → Service → Repository                      │
│                                                         │
│  Módulos: auth · users · pilots · flights               │
│           reports · certifications · documents          │
│           discord · pub (public stats)                  │
└────────────────────────┬────────────────────────────────┘
                         │ JDBC + SSL
┌────────────────────────▼────────────────────────────────┐
│              PostgreSQL (Supabase)                       │
│  Flyway migrations · ddl-auto=update (dev)              │
└─────────────────────────────────────────────────────────┘
```

### Decisões de design

**Por que JWT stateless?**
O Render free hiberna após 15 min de inatividade. Com sessões server-side, cada cold start invalidaria todas as sessões ativas. JWT stateless resolve isso — o token é válido independente do estado do servidor.

**Por que `@PreAuthorize` por método em vez de regras no `SecurityConfig`?**
Mais legível e mais fácil de manter. A regra de acesso fica junto ao método que ela protege, não em um arquivo centralizado que cresce indefinidamente.

**Por que Flyway mesmo com `ddl-auto=update`?**
`ddl-auto=update` não consegue reverter mudanças nem recriar constraints (ex: `CHECK` em enums PostgreSQL). Flyway garante que o schema em produção (Supabase) seja idêntico ao local, mesmo sem acesso SSH ao banco.

**Por que o campo `grupo` existe se o frontend já tem o `rankName`?**
Porque a lógica de grupo não é 1:1 com rank. ADM tem rank LEAD mas grupo "adm". Centralizar em `resolveGrupo()` no backend evita que cada cliente reimplemente a mesma regra de forma inconsistente.

**Por que filtrar ADM no banco e não no frontend?**
O frontend pode ser bypassado. A regra `WHERE user.role != 'ADM'` na query JPQL garante que o ADM nunca apareça em nenhuma listagem, mesmo que o frontend seja inspecionado ou a API seja chamada diretamente.

---

## API Reference

### Auth
| Método | Rota | Acesso | Descrição |
|--------|------|--------|-----------|
| POST | `/auth/setup` | Público | Cria primeiro LEAD. 403 se já existirem usuários |
| POST | `/auth/login` | Público | Retorna JWT |
| POST | `/auth/register` | LEAD/ADM/SUPERVISOR | Cadastra novo membro |
| POST | `/auth/refresh` | Autenticado | Renova token |
| PATCH | `/auth/email` | Autenticado | Altera e-mail (exige senha atual) |
| PATCH | `/auth/password` | Autenticado | Altera senha (exige senha atual) |

### Pilotos
| Método | Rota | Acesso | Descrição |
|--------|------|--------|-----------|
| GET | `/pilots` | Autenticado | Lista todos (exclui ADM), ordenado por score |
| GET | `/pilots/:id` | Autenticado | Perfil completo |
| POST | `/pilots` | LEAD/ADM/SUPERVISOR | Cria piloto |
| PUT | `/pilots/:id` | LEAD/ADM/SUPERVISOR | Edição completa (callsign, status, rank, foto) |
| PATCH | `/pilots/:id/rank` | LEAD/ADM | Altera rank manualmente |
| PATCH | `/pilots/:id/profile` | Autenticado (próprio) | Edita callsign e foto (sem rank/status) |
| DELETE | `/pilots/:id` | LEAD/ADM | Remove piloto |

### Voos, Relatórios, Certificações, Documentos
Veja a documentação interativa em `/swagger-ui/index.html`.

---

## Rodando Localmente

### Pré-requisitos
- Java 21
- Docker Desktop
- Node.js 20+

### Backend

```bash
# Clone o repositório
git clone https://github.com/bokinhass/air-ops-system
cd air-ops-system

# Crie o .env na raiz (necessário para o Docker Compose)
cat > .env << EOF
POSTGRES_DB=air_ops
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
JWT_SECRET=air-ops-system-local-dev-secret-1234567890
CORS_ALLOWED_ORIGIN=http://localhost:3000
EOF

# Rodar — Spring Boot sobe o container PostgreSQL automaticamente
./mvnw spring-boot:run
```

O Spring Boot detecta o `compose.yaml`, sobe o container `air_ops_postgres` via Docker e aguarda o healthcheck antes de inicializar a API.

Acesse: `http://localhost:8080/swagger-ui/index.html`

### Frontend

```bash
cd air-ops-system-web

# Crie o .env.local
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local

npm install
npm run dev
```

Acesse: `http://localhost:3000`

### Primeiro acesso

```bash
# Cria o usuário LEAD inicial (só funciona com banco vazio)
curl -X POST http://localhost:8080/auth/setup \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@asd.com","password":"senha123"}'
```

---

## Deploy

### Banco — Supabase
1. Crie um projeto em [supabase.com](https://supabase.com)
2. Copie a connection string JDBC: `jdbc:postgresql://db.[ref].supabase.co:5432/postgres?sslmode=require`

### Backend — Render
Configure as variáveis de ambiente no painel do Render:

| Variável | Valor |
|----------|-------|
| `DATABASE_URL` | Connection string JDBC do Supabase |
| `POSTGRES_USER` | `postgres` |
| `POSTGRES_PASSWORD` | Senha do Supabase |
| `JWT_SECRET` | String aleatória ≥ 32 chars |
| `CORS_ALLOWED_ORIGIN` | URL do frontend na Vercel |
| `DISCORD_WEBHOOK_REPORTS` | URL do webhook Discord (opcional) |

### Frontend — Vercel
Configure no painel da Vercel:

| Variável | Valor |
|----------|-------|
| `NEXT_PUBLIC_API_URL` | URL da API no Render |

---

## Estrutura de Pacotes

```
com.air_ops_system/
├── auth/           JWT filter, AuthService, DTOs de login/registro/settings
├── users/          User entity, UserController, UserService
├── pilots/         Pilot entity, Rank, PilotService, RankSeeder
├── flights/        FlightLog entity, FlightService, enums Aircraft/FlightType
├── reports/        PerformanceReport, ReportService, cálculo de score
├── certifications/ Certification entity, CertificationService
├── documents/      Document entity, DocumentService
├── discord/        DiscordWebhookService (RestClient, silencioso se sem URL)
├── pub/            PublicStatsService, endpoint sem autenticação
└── config/         SecurityConfig, FlywayRunner, RankSeeder, OpenApiConfig
```

---

## Como foi desenvolvido

Este projeto foi construído iterativamente com **Claude Code** como par de programação. O fluxo de trabalho foi:

1. Definir o que precisava ser feito (regra de negócio, endpoint, componente)
2. Entender o código que seria gerado antes de aplicar
3. Testar localmente
4. Iterar — corrigir bugs, refinar comportamento, adicionar casos de borda

O objetivo não era só ter o código funcionando, mas **entender cada decisão** para conseguir manter e evoluir o sistema de forma independente.

Usar IA como ferramenta de desenvolvimento acelerou muito a produção, mas o conhecimento sobre o que foi construído e por quê continua sendo meu.

---

## Próximos passos

- [ ] Paginação em `/flights` e `/reports`
- [ ] Painel admin com soft delete de usuários
- [ ] Rate limiting e headers de segurança (Helmet)
- [ ] Logs estruturados (Pino/Winston equivalente em Java: Logback JSON)
- [ ] Audit log — tabela `audit_logs` com histórico de ações críticas
- [ ] Índices no banco para queries de score e status

---

## Licença

MIT — sinta-se livre para usar como referência ou ponto de partida.
