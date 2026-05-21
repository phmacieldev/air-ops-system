# Air Ops System

API backend para controle de operações de voo, usuários e perfis de acesso.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT com JJWT
- Bean Validation
- PostgreSQL
- Maven
- Springdoc OpenAPI / Swagger
- Docker Compose

## Funcionalidades Implementadas

- Cadastro de usuário com senha criptografada usando BCrypt.
- Login com validação de senha e retorno de JWT.
- Autenticação stateless com JWT.
- Filtro JWT para proteger rotas privadas.
- Rota protegida para perfil do usuário autenticado.
- DTOs para entrada e saída de dados.
- Validações com `@NotBlank`, `@Email` e `@Size`.
- Exceptions personalizadas e handler global com `@RestControllerAdvice`.
- Swagger configurado com autenticação Bearer Token.
- Configuração local por variáveis de ambiente.

## Estrutura Principal

```txt
auth/
  controller/
  dto/
  filter/
  service/

users/
  controller/
  domain/
  dto/
  repository/

common/
  dto/
  exception/

config/
```

## Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com base no `.env.example`:

```env
POSTGRES_DB=air_ops
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
JWT_SECRET=air-ops-system-local-dev-secret-1234567890
```

O arquivo `.env` é local e não deve ser enviado para o GitHub.

## Subir o Banco

```powershell
docker compose up -d
```

O PostgreSQL ficará disponível em:

```txt
localhost:5432
```

Banco padrão:

```txt
air_ops
```

## Rodar a API

No PowerShell:

```powershell
$env:POSTGRES_PASSWORD='1234'
$env:JWT_SECRET='air-ops-system-local-dev-secret-1234567890'
.\mvnw spring-boot:run
```

Pelo IntelliJ IDEA, rode a classe:

```txt
AirOpsSystemApplication
```

Para o DevTools reiniciar a aplicação após alterações, ative:

```txt
Settings > Build, Execution, Deployment > Compiler > Build project automatically
```

Se o reinício automático não acontecer ao salvar, use:

```txt
Build > Build Project
```

ou:

```txt
Ctrl + F9
```

## Endpoints

### Cadastro

```txt
POST /auth/register
```

Body:

```json
{
  "name": "Pedro",
  "email": "pedro@email.com",
  "password": "123456"
}
```

Resposta:

```json
{
  "token": "jwt_aqui"
}
```

### Login

```txt
POST /auth/login
```

Body:

```json
{
  "email": "pedro@email.com",
  "password": "123456"
}
```

Resposta:

```json
{
  "token": "jwt_aqui"
}
```

### Perfil do Usuário Autenticado

```txt
GET /me
```

Essa rota é protegida. Envie o token no header:

```txt
Authorization: Bearer jwt_aqui
```

Resposta:

```json
{
  "id": "uuid-do-usuario",
  "name": "Pedro",
  "email": "pedro@email.com",
  "role": "TRAINEE",
  "createdAt": "2026-05-20T17:00:00"
}
```

### Rota de Teste Protegida

```txt
GET /asd/test
```

Resposta:

```txt
API funcionando
```

## Swagger

Com a aplicação rodando, acesse:

```txt
http://localhost:8080/swagger-ui/index.html
```

Fluxo de teste:

1. Execute `POST /auth/register` ou `POST /auth/login`.
2. Copie o token retornado.
3. Clique em `Authorize`.
4. Cole o token JWT.
5. Execute uma rota protegida, como `GET /me`.

## Segurança

O projeto usa JWT com autenticação stateless.

Rotas liberadas:

```txt
/auth/**
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

Todas as demais rotas exigem token JWT válido.

## Erros Padronizados

A API possui handler global para retornar erros em formato consistente.

Exemplo:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou senha inválidos",
  "path": "/auth/login",
  "timestamp": "2026-05-20T17:00:00"
}
```

Casos tratados:

- Email já cadastrado.
- Credenciais inválidas.
- Dados inválidos enviados nos DTOs.

## Validações

Os DTOs de autenticação validam:

- campos obrigatórios;
- formato de email;
- tamanho mínimo da senha no cadastro.

## Desenvolvimento

Arquivos locais de controle, como `todo.md` e `todo2.md`, são ignorados pelo Git através da regra:

```txt
todo*.md
```
