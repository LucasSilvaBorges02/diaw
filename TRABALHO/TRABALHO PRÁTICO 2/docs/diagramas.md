# Modelos UML

## 1. Diagrama de Casos de Uso

```mermaid
flowchart LR
    C([Cliente])
    A([Agente])
    E([Empresa]) -->|é um| A
    B([Banco]) -->|é um| A

    subgraph Sistema de Aluguel de Carros
      UC0((Cadastrar-se))
      UC1((Fazer login))
      UC2((Manter cadastro))
      UC3((Introduzir pedido))
      UC4((Consultar pedidos))
      UC5((Modificar pedido))
      UC6((Cancelar pedido))
      UC7((Avaliar pedido))
      UC8((Executar contrato))
      UC9((Conceder crédito))
      UC10((Manter automóveis))
      UC11((Manter clientes))
    end

    C --- UC0 & UC1 & UC2 & UC3 & UC4 & UC5 & UC6
    A --- UC1 & UC4 & UC5 & UC7 & UC8 & UC10 & UC11
    B --- UC9
    UC8 -.->|extend| UC9
```

## 2. Diagrama de Classes

### Versão 1 (modelo inicial)

```mermaid
classDiagram
    class Usuario { +login +senha }
    class Cliente { +rg +cpf +nome +endereco +profissao }
    class Empregador { +nome +rendimento }
    class Empresa
    class Banco
    class Automovel { +matricula +ano +marca +modelo +placa }
    class Pedido { +data +status }
    class Contrato
    class ContratoCredito
    Usuario <|-- Cliente
    Usuario <|-- Empresa
    Usuario <|-- Banco
    Cliente "1" --> "0..3" Empregador
    Cliente "1" --> "*" Pedido
    Pedido --> Automovel
    Pedido --> "0..1" Contrato
    Contrato --> "0..1" ContratoCredito
    Banco --> ContratoCredito
```

### Versão 2 (final, igual ao código)

```mermaid
classDiagram
    class Usuario {
      <<abstract>>
      -Long id
      -String nome
      -String login
      -String senha
      +getPapel() Papel
    }
    class Cliente {
      -String rg
      -String cpf
      -String endereco
      -String profissao
      +getRendaTotal() BigDecimal
      +substituirRendimentos(List)
    }
    class Rendimento {
      <<embeddable>>
      -String entidadeEmpregadora
      -BigDecimal valor
    }
    class Agente {
      -String cnpj
      -TipoAgente tipo
      +isBanco() boolean
    }
    class Automovel {
      -String matricula
      -Integer ano
      -String marca
      -String modelo
      -String placa
      -BigDecimal valorDiaria
    }
    class Pedido {
      -LocalDate dataInicio
      -LocalDate dataFim
      -StatusPedido status
      -String parecer
      +alterar()
      +cancelar()
      +avaliar(Agente, boolean, String)
      +registrarContrato(Contrato)
      +getValorTotal() BigDecimal
    }
    class Contrato {
      -TipoProprietario tipoProprietario
      -LocalDateTime assinadoEm
    }
    class ContratoCredito {
      -BigDecimal valor
    }
    class StatusPedido { <<enum>> PENDENTE APROVADO REPROVADO CANCELADO CONTRATADO }
    class TipoAgente { <<enum>> EMPRESA BANCO }
    class TipoProprietario { <<enum>> CLIENTE EMPRESA BANCO }

    Usuario <|-- Cliente
    Usuario <|-- Agente
    Cliente "1" *-- "0..3" Rendimento
    Cliente "1" <-- "*" Pedido
    Pedido "*" --> "1" Automovel
    Pedido "*" --> "0..1" Agente : avaliador
    Pedido "1" -- "0..1" Contrato
    Contrato "*" --> "1" Usuario : proprietario
    Contrato "*" --> "1" Agente : executado por
    Contrato "1" *-- "0..1" ContratoCredito
    ContratoCredito "*" --> "1" Agente : banco
```

**O que mudou da v1 para a v2**

- `Empresa` e `Banco` viraram uma classe só, `Agente`, com o enum `TipoAgente`: os dois fazem as mesmas coisas, só o crédito é exclusivo do banco.
- `Empregador` virou `Rendimento` (embutido no cliente, no máximo 3).
- O nome passou para `Usuario`, porque agentes também têm nome.
- `Automovel` ganhou `valorDiaria` para o agente conseguir analisar o valor do pedido.
- `Contrato` guarda em nome de quem o carro fica (`TipoProprietario` + `proprietario`).

## 3. Diagrama de Pacotes (visão lógica)

```mermaid
flowchart TB
  subgraph Front-end [frontend - React + Vite]
    pages --> components
    pages --> services
    pages --> context
    context --> services
  end
  subgraph Back-end [backend - br.pucminas.aluguel]
    controller --> service
    controller --> dto
    service --> repository
    service --> model
    service --> security
    dto --> model
    repository --> model
    config --> security
    exception
  end
  services -->|HTTP/JSON + JWT| controller
  repository -->|JPA| DB[(Supabase PostgreSQL)]
```
