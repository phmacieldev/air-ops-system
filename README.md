# Air Ops System

API backend para controle de operações de voo, usuários e perfis de acesso.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- Springdoc OpenAPI / Swagger
- Docker Compose

## O que já foi feito

- Estrutura inicial do projeto Spring Boot.
- Entidade `User` criada.
- Enum `Role` criado com os perfis:
    - `ADMIN`
    - `LEAD`
    - `SUPERVISOR`
    - `INSTRUCTOR`
    - `PILOT`
    - `TRAINEE`
- Repositório `UserRepository` criado.
- Configuração de conexão com PostgreSQL.
- Configuração para criação/atualização automática das tabelas com JPA.
- Rota de teste criada:

```txt
GET /asd/test
```

Resposta:

```txt
API funcionando
```

- Swagger configurado.
- DevTools adicionado para facilitar o desenvolvimento.
- Variáveis de ambiente preparadas para evitar subir senha real no GitHub.

## Configuração local

Crie um arquivo `.env` na raiz do projeto com base no arquivo `.env.example`:

```env
POSTGRES_DB=air_ops
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
```

O arquivo `.env` não deve ser enviado para o GitHub.

## Subir o banco com Docker Compose

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

No PowerShell, defina a senha do banco na sessão atual:

```powershell
$env:POSTGRES_PASSWORD='1234'
```

Depois rode:

```powershell
.\mvnw spring-boot:run
```

## Rodar pelo IntelliJ IDEA

Você também pode rodar a aplicação diretamente pelo IntelliJ IDEA usando a classe:

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

ou o atalho:

```txt
Ctrl + F9
```

## Testar a rota

Com a aplicação rodando, acesse:

```txt
http://localhost:8080/asd/test
```

Retorno esperado:

```txt
API funcionando
```

## Swagger

Com a aplicação rodando, acesse:

```txt
http://localhost:8080/swagger-ui/index.html
```

No Swagger, procure:

```txt
GET /asd/test
```

Clique em `Try it out` e depois em `Execute`.

## Observação sobre segurança

O projeto já possui Spring Security. Enquanto não houver uma configuração própria de segurança, algumas rotas podem
pedir login automaticamente.

Quando isso acontecer, use o usuário padrão:

```txt
user
```

A senha aparece no terminal ao iniciar a aplicação, em uma linha parecida com:

```txt
Using generated security password: ...
```