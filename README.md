# Desafio Itaú Backend

API REST desenvolvida em Java com Spring Boot para o desafio técnico do Itaú.

A aplicação permite receber transações, limpar transações armazenadas em memória e consultar estatísticas das transações realizadas dentro de uma janela de tempo configurável.

## Tecnologias utilizadas

- Java 17
- Spring Boot 4
- Spring Web MVC
- Bean Validation
- Springdoc OpenAPI / Swagger
- Docker
- Maven
- JUnit
- MockMvc

## Funcionalidades

- Registrar transações em memória
- Validar transações recebidas
- Rejeitar transações com valor negativo
- Rejeitar transações com data/hora no futuro
- Rejeitar requisições com JSON inválido
- Limpar todas as transações
- Calcular estatísticas das transações recentes
- Configurar a quantidade de segundos usada no cálculo das estatísticas
- Documentar a API com Swagger
- Retornar erros padronizados em JSON
- Executar testes automatizados

## Regras da API

A API segue as seguintes regras principais:

- Os dados são armazenados somente em memória
- Não utiliza banco de dados
- Não utiliza cache externo
- As requisições e respostas usam JSON
- Transações futuras são rejeitadas
- Transações com valor negativo são rejeitadas
- As estatísticas consideram apenas as transações dentro da janela configurada
- Por padrão, a janela de estatísticas é de 60 segundos

## Endpoints

### POST `/transacao`

Recebe uma transação e a armazena em memória caso ela seja válida.

#### Exemplo de requisição

```json
{
  "valor": 123.45,
  "dataHora": "2026-04-25T10:30:00.000-03:00"
}
```

#### Campos da requisição

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `valor` | `number` | Sim | Valor da transação. Deve ser maior ou igual a zero. |
| `dataHora` | `string` | Sim | Data e hora da transação no padrão ISO 8601. Não pode estar no futuro. |

#### Respostas

| Status | Descrição |
|---|---|
| `201 Created` | Transação aceita e registrada com sucesso |
| `400 Bad Request` | JSON inválido ou requisição malformada |
| `422 Unprocessable Entity` | Transação inválida |

---

### DELETE `/transacao`

Remove todas as transações armazenadas em memória.

#### Respostas

| Status | Descrição |
|---|---|
| `200 OK` | Transações removidas com sucesso |

---

### GET `/estatistica`

Retorna as estatísticas das transações realizadas dentro da janela de tempo configurada.

Por padrão, a janela considerada é de 60 segundos.

#### Exemplo de resposta

```json
{
  "count": 10,
  "sum": 1234.56,
  "avg": 123.456,
  "min": 12.34,
  "max": 123.56
}
```

#### Campos da resposta

| Campo | Tipo | Descrição |
|---|---|---|
| `count` | `number` | Quantidade de transações dentro da janela configurada |
| `sum` | `number` | Soma total dos valores transacionados |
| `avg` | `number` | Média dos valores transacionados |
| `min` | `number` | Menor valor transacionado |
| `max` | `number` | Maior valor transacionado |

#### Resposta quando não houver transações

Quando não houver transações dentro da janela configurada, todos os valores retornam como zero.

```json
{
  "count": 0,
  "sum": 0.0,
  "avg": 0.0,
  "min": 0.0,
  "max": 0.0
}
```

#### Respostas

| Status | Descrição |
|---|---|
| `200 OK` | Estatísticas calculadas com sucesso |

## Tratamento de erros

A API possui tratamento global de erros com `RestControllerAdvice`.

Erros de validação, JSON inválido, regras de negócio e erros internos são retornados em formato padronizado.

### Exemplo de erro de validação

```json
{
  "timestamp": "2026-04-25T10:30:00.000-03:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação nos campos da requisição.",
  "path": "/transacao",
  "errors": [
    {
      "field": "valor",
      "message": "O valor da transação deve ser maior ou igual a zero."
    }
  ]
}
```

### Exemplo de erro por data futura

```json
{
  "timestamp": "2026-04-25T10:30:00.000-03:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "A transação enviada é inválida.",
  "path": "/transacao",
  "errors": [
    {
      "field": "dataHora",
      "message": "A data e hora da transação não pode estar no futuro."
    }
  ]
}
```

### Exemplo de erro por JSON inválido

```json
{
  "timestamp": "2026-04-25T10:30:00.000-03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "A requisição possui JSON inválido, campos com tipos incorretos ou data em formato inválido.",
  "path": "/transacao",
  "errors": []
}
```

## Configuração da janela de estatísticas

Por padrão, o endpoint `GET /estatistica` considera as transações realizadas nos últimos 60 segundos.

Esse valor pode ser alterado no arquivo:

```txt
src/main/resources/application.properties
```

Propriedade:

```properties
app.statistics.window-seconds=60
```

Exemplo para considerar os últimos 120 segundos:

```properties
app.statistics.window-seconds=120
```

Também é possível alterar essa configuração via variável de ambiente:

```txt
APP_STATISTICS_WINDOW_SECONDS=120
```

## Documentação com Swagger

A API possui documentação interativa com Swagger UI.

Após subir a aplicação, acesse:

```txt
http://localhost:8080/swagger-ui.html
```

Ou:

```txt
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI em JSON fica disponível em:

```txt
http://localhost:8080/v3/api-docs
```

## Como rodar com Docker

### Windows PowerShell

Na raiz do projeto, execute:

```powershell
.\run.ps1
```

A aplicação ficará disponível em:

```txt
http://localhost:8080
```

```txt
http://localhost:8080/swagger-ui/index.html
```

Para parar a aplicação:

```powershell
.\stop.ps1
```

### Linux, Mac ou WSL

Se tiver `make` instalado, execute:

```bash
make run
```

Para parar:

```bash
make stop
```

Para visualizar logs:

```bash
make logs
```

## Rodando manualmente com Docker

Caso prefira rodar sem scripts:

```bash
docker build -t desafio-itau-backend-api .
```

```bash
docker run -d --name desafio-itau-backend-container -p 8080:8080 desafio-itau-backend-api
```

Para parar e remover o container:

```bash
docker stop desafio-itau-backend-container
docker rm desafio-itau-backend-container
```

## Rodando com janela customizada via Docker

Exemplo usando 120 segundos:

```bash
docker run -d \
  --name desafio-itau-backend-container \
  -p 8080:8080 \
  -e APP_STATISTICS_WINDOW_SECONDS=120 \
  desafio-itau-backend-api
```

No Windows PowerShell:

```powershell
docker run -d `
  --name desafio-itau-backend-container `
  -p 8080:8080 `
  -e APP_STATISTICS_WINDOW_SECONDS=120 `
  desafio-itau-backend-api
```

## Testes automatizados

O projeto possui testes automatizados para validar regras de negócio e comportamento dos endpoints.

Os testes cobrem cenários como:

- criação de transação válida
- rejeição de transação com valor negativo
- rejeição de transação com data futura
- rejeição de requisição com campos obrigatórios ausentes
- rejeição de JSON inválido
- limpeza de transações
- cálculo de estatísticas com transações recentes
- ignorar transações fora da janela configurada
- retorno de estatísticas zeradas quando não houver transações

### Rodar testes no Windows com Docker

Caso não tenha Maven instalado localmente, execute:

```powershell
.\test.ps1
```

### Rodar testes localmente com Maven

```bash
mvn test
```

## Build local

Caso tenha Maven instalado:

```bash
mvn clean package
```

Para rodar localmente:

```bash
mvn spring-boot:run
```

## Estrutura do projeto

```txt
src/
└── main/
    └── java/
        └── desafio/
            └── itau/
                └── springboot/
                    ├── config/
                    │   ├── OpenApiConfig.java
                    │   └── StatisticsProperties.java
                    ├── controller/
                    │   ├── TransactionController.java
                    │   └── StatisticsController.java
                    ├── dto/
                    │   ├── TransactionRequest.java
                    │   ├── StatisticsResponse.java
                    │   ├── ErrorResponse.java
                    │   └── FieldErrorResponse.java
                    ├── exception/
                    │   ├── GlobalExceptionHandler.java
                    │   └── InvalidTransactionException.java
                    ├── model/
                    │   └── Transaction.java
                    └── service/
                        └── TransactionService.java
```

## Exemplos de uso

### Criar uma transação válida

```bash
curl -i -X POST http://localhost:8080/transacao \
  -H "Content-Type: application/json" \
  -d '{"valor":123.45,"dataHora":"2026-04-25T10:30:00.000-03:00"}'
```

### Criar uma transação inválida

```bash
curl -i -X POST http://localhost:8080/transacao \
  -H "Content-Type: application/json" \
  -d '{"valor":-10.0,"dataHora":"2026-04-25T10:30:00.000-03:00"}'
```

### Consultar estatísticas

```bash
curl -i http://localhost:8080/estatistica
```

### Limpar transações

```bash
curl -i -X DELETE http://localhost:8080/transacao
```

## Observações técnicas

A aplicação utiliza uma estrutura em memória baseada em uma fila concorrente para armazenar as transações durante a execução.

Ao reiniciar a aplicação, todas as transações são perdidas, pois o projeto não utiliza banco de dados ou cache externo.

O cálculo estatístico é feito usando `DoubleSummaryStatistics`, considerando apenas as transações cuja data/hora esteja dentro da janela configurada.

## Autor

Projeto desenvolvido para o desafio técnico de backend do Itaú.