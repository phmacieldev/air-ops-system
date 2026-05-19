# AIR OPS SYSTEM — ROADMAP PARTE 1

## Fundação do Projeto

---

# OBJETIVO DA PARTE 1

Ter um backend funcional com:

* Projeto Spring criado
* PostgreSQL funcionando via Docker
* Banco conectado
* Estrutura organizada
* Entidade User criada
* Swagger funcionando
* Endpoint de teste funcionando
* Projeto versionado no GitHub

---

# STACK DEFINIDA

## Backend

* Java 21
* Spring Boot 3.5+
* Maven
* PostgreSQL
* Spring Security
* JWT
* Docker
* Swagger/OpenAPI

---

## Frontend (mais pra frente)

* NextJS 15+
* TypeScript
* TailwindCSS
* Shadcn/ui

---

# FERRAMENTAS

## IDE Backend

IntelliJ IDEA Community

## IDE Frontend

VSCode

## Banco

PostgreSQL

## Containers

Docker Desktop

---

# REGRAS IMPORTANTES

## NÃO fazer ainda

* Frontend
* JWT
* Dashboard
* Integração Discord
* Upload
* Relatórios
* Microserviços

---

# META DESSA FASE

Ao final você deve ter:

* Backend rodando
* Banco conectado
* Estrutura organizada
* User entity criada
* Swagger funcionando
* GitHub configurado

---

# ETAPA 1 — CRIAR PROJETO SPRING

## Spring Initializr

Configuração:

### Project

Maven

### Language

Java

### Spring Boot

3.5.x

### Java

21

---

## Dependências

Adicionar:

* Spring Web
* Spring Data JPA
* PostgreSQL Driver
* Spring Security
* Validation
* Lombok
* Docker Compose Support

---

## Nome do projeto

```txt
air-ops-system
```

---

# CHECKLIST ETAPA 1

```txt
[x] Projeto criado
[x] Projeto abriu no IntelliJ
[x] Maven carregou dependências
[x] Projeto roda sem erro
```

---

# ETAPA 2 — ESTRUTURA INICIAL

## Criar packages

```txt
config
auth
users
pilots
common
exceptions
security
```

---

# CHECKLIST ETAPA 2

```txt
[x] Packages criados
[x] Estrutura organizada
```

---

# ETAPA 3 — DOCKER + POSTGRESQL

## Criar arquivo

```txt
docker-compose.yml
```

---

## Conteúdo inicial

```yaml
services:
  postgres:
    image: postgres:16
    container_name: air_ops_postgres

    environment:
      POSTGRES_DB: air_ops
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres

    ports:
      - "5432:5432"
```

---

## Rodar container

```bash
docker compose up -d
```

---

# CHECKLIST ETAPA 3

```txt
[x] Docker funcionando
[x] Container PostgreSQL rodando
[x] Banco criado
[x] Porta 5432 funcionando
```

---

# ETAPA 4 — CONFIGURAR CONEXÃO COM BANCO

## Criar arquivo

```txt
src/main/resources/application.yml
```

---

## Configuração

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/air_ops
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: update

    show-sql: true

    properties:
      hibernate:
        format_sql: true
```

---

# CHECKLIST ETAPA 4

```txt
[x] Aplicação conecta no banco
[x] Sem erro de datasource
[x] Hibernate criando tabelas
```

---

# ETAPA 5 — CRIAR ENTIDADE USER

## Campos mínimos

```txt
id
name
email
password
role
createdAt
```

---

## Criar enum Role

```txt
ADMIN
LEAD
SUPERVISOR
INSTRUCTOR
PILOT
TRAINEE
```

---

## IMPORTANTE

Tentar fazer sozinho primeiro.

Mesmo que erre.

---

# CHECKLIST ETAPA 5

```txt
[x] Entity User criada
[x] Enum Role criado
[x] Repository criado
[x] Tabela user criada no banco
```

---

# ETAPA 6 — SWAGGER / OPENAPI

## Dependência Maven

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

---

## URL Swagger

```txt
http://localhost:8080/swagger-ui.html
```

---

# CHECKLIST ETAPA 6

```txt
[x] Swagger funcionando
[x] Endpoint aparece
```

---

# ETAPA 7 — ENDPOINT TESTE

## Criar controller

Endpoint:

```txt
GET /test
```

---

## Resposta esperada

```json
{
  "message": "API funcionando"
}
```

---

# CHECKLIST ETAPA 7

```txt
[x] Controller criado
[x] Endpoint funcionando
[x] Swagger mostrando endpoint
```

---

# ETAPA 8 — GIT E GITHUB

## Criar repositório

Sugestão:

```txt
air-ops-system
```

---

## Fazer upload projeto

---

# CHECKLIST ETAPA 8

```txt
[ ] Git iniciado
[ ] Primeiro commit
[ ] Projeto no GitHub
```

---

# O QUE VOCÊ DEVE APRENDER NESSA FASE

## Java/Spring

* Estrutura projeto
* JPA
* Entidade
* Repository
* Configuração
* Maven

---

## Infra

* Docker
* Container
* Banco
* Conexão

---

## Backend

* API
* Controller
* Organização
* Arquitetura inicial

---

# IMPORTANTE

## NÃO avance enquanto

* algo estiver quebrado
* você não entender minimamente
* não conseguir explicar o que fez

---

# OBJETIVO FINAL DA PARTE 1

```txt
✔ Projeto Spring funcionando
✔ PostgreSQL rodando em Docker
✔ User entity criada
✔ Banco conectado
✔ Swagger funcionando
✔ Endpoint teste funcionando
✔ Projeto no GitHub
```
