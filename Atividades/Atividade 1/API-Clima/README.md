# API REST de Clima - Belo Horizonte

API REST desenvolvida com Spring Boot que consulta informações meteorológicas
de Belo Horizonte - MG através da API externa Open-Meteo.

Atividade 01 - Desenvolvimento e Integração de Aplicações Web - PUC Minas

## Integrantes

- Lucas Silva
- Arthur Monserrat

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Maven
- API externa: [Open-Meteo](https://open-meteo.com/)

## Dependências

| Dependência | Finalidade |
|---|---|
| `spring-boot-starter-webmvc` | Construção da API REST (Spring MVC + Tomcat + Jackson) |
| `spring-boot-starter-restclient` | Cliente HTTP para consumo da API externa |
| `spring-boot-starter-webmvc-test` | Dependências de teste |

## Configuração

A Open-Meteo é gratuita e **não exige API Key**, então nenhuma credencial
precisa ser configurada para executar o projeto.

As configurações ficam em `src/main/resources/application.properties`:

```properties
clima.api.url=https://api.open-meteo.com/v1/forecast
clima.geocoding.url=https://geocoding-api.open-meteo.com/v1/search
clima.cidade=Belo Horizonte - MG
clima.latitude=-19.9167
clima.longitude=-43.9345
```

Caso o serviço externo fosse trocado por um que exija chave, a propriedade
deveria ser adicionada nesse mesmo arquivo ou definida como variável de
ambiente, nunca diretamente no código-fonte.

## Como executar

Pré-requisitos: JDK 25 instalado.

```bash
cd API-Clima
./mvnw spring-boot:run
```

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Endpoints

### GET /climaBH

Retorna as condições meteorológicas atuais de Belo Horizonte - MG.
As coordenadas são lidas do `application.properties`.

**Exemplo de resposta (200 OK):**

```json
{
  "cidade": "Belo Horizonte - MG",
  "latitude": -19.9297,
  "longitude": -43.966034,
  "temperatura": 20.1,
  "umidade": 80,
  "velocidadeVento": 6.2,
  "direcaoVentoGraus": 68,
  "direcaoVento": "Nordeste",
  "temperaturaMaxima": 22.3,
  "temperaturaMinima": 16.6,
  "descricao": "Predominantemente limpo",
  "dataHoraConsulta": "27/08/2026 18:00:00"
}
```

**Campos retornados:**

| Campo | Descrição | Unidade |
|---|---|---|
| `cidade` | Localização consultada | - |
| `latitude` / `longitude` | Coordenadas do ponto retornado pela API | graus |
| `temperatura` | Temperatura atual | °C |
| `umidade` | Umidade relativa do ar | % |
| `velocidadeVento` | Velocidade do vento | km/h |
| `direcaoVentoGraus` | Direção do vento em graus | ° |
| `direcaoVento` | Direção do vento por extenso | - |
| `temperaturaMaxima` | Máxima prevista para o dia | °C |
| `temperaturaMinima` | Mínima prevista para o dia | °C |
| `descricao` | Condição do tempo (traduzida do código WMO) | - |
| `dataHoraConsulta` | Momento da consulta | - |

### GET /clima/{cidade}

Retorna as condições meteorológicas de qualquer cidade informada na URL.
O nome é resolvido em coordenadas pela API de geocoding da Open-Meteo, e
essas coordenadas são então usadas na consulta de previsão.

Nomes compostos podem usar hífen no lugar do espaço.

**Exemplos:**

```text
GET /clima/curitiba
GET /clima/belo-horizonte
GET /clima/sao-paulo
```

A resposta tem o mesmo formato do `/climaBH`. O campo `cidade` traz o nome
como reconhecido pelo geocoding, acompanhado do estado quando disponível
(ex: `"Curitiba - Paraná"`). Quando a busca retorna mais de um resultado,
é utilizado o primeiro, de maior relevância segundo a Open-Meteo.

## Tratamento de erros

**404 Not Found** — cidade informada não encontrada pelo geocoding:

```json
{
  "erro": "Cidade nao encontrada: xyzabc",
  "status": 404,
  "dataHora": "27/08/2026 18:45:12"
}
```

**503 Service Unavailable** — falha de comunicação com a API externa ou
dados indisponíveis:

```json
{
  "erro": "Falha na comunicacao com a API de clima.",
  "status": 503,
  "dataHora": "27/08/2026 18:45:12"
}
```

**500 Internal Server Error** — erro inesperado na aplicação.

O tratamento é centralizado na classe `TratadorDeErros`, anotada com
`@RestControllerAdvice`.

## Estrutura do projeto

```text
src/main/java/com/example/API_Clima/
├── ApiClimaApplication.java
├── controller/
│   └── Controller.java              # expõe os endpoints REST
├── service/
│   └── Service.java                 # consome as APIs externas e processa os dados
├── dto/
│   ├── OpenMeteoResponse.java       # espelha o JSON da previsão
│   ├── GeocodingResponse.java       # espelha o JSON do geocoding
│   └── ClimaDTO.java                # objeto próprio de resposta da aplicação
└── exception/
    ├── ClimaException.java
    ├── CidadeNaoEncontradaException.java
    └── TratadorDeErros.java
```

## Desafio extra

Dos itens sugeridos como desafio adicional, foram implementados:

- Consulta do clima de outras cidades (`GET /clima/{cidade}`)
- Retorno das informações organizadas em um objeto próprio da aplicação (`ClimaDTO`)