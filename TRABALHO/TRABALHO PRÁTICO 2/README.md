# 🚗 Rota Livre: Sistema de Aluguel de Carros

> [!NOTE]
> Sistema web para gerir aluguéis de automóveis: clientes fazem, modificam, consultam e cancelam pedidos pela internet, e agentes (empresas e bancos) analisam, aprovam e transformam os pedidos em contratos, com crédito opcional de um banco.

![Java](https://img.shields.io/badge/Java-25-007ec6?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.9-007ec6?style=for-the-badge&logo=springboot&logoColor=white) ![Maven](https://img.shields.io/badge/Maven-wrapper-007ec6?style=for-the-badge&logo=apachemaven&logoColor=white) ![React](https://img.shields.io/badge/React-19-007ec6?style=for-the-badge&logo=react&logoColor=white) ![Vite](https://img.shields.io/badge/Vite-8-007ec6?style=for-the-badge&logo=vite&logoColor=white) ![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-007ec6?style=for-the-badge&logo=supabase&logoColor=white)

Laboratório 02 de **Desenvolvimento de Interfaces e Aplicações Web (DIAW)**, Engenharia de Software, PUC Minas.

---

## 📚 Índice
- [Links Úteis](#-links-úteis)
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Modelagem (UML)](#-modelagem-uml)
- [Instalação e Execução](#-instalação-e-execução)
- [Deploy](#-deploy)
- [Estrutura de Pastas](#-estrutura-de-pastas)
- [Testes](#-testes)
- [Documentações utilizadas](#-documentações-utilizadas)
- [Autores](#-autores)
- [Agradecimentos](#-agradecimentos)

---

## 🔗 Links Úteis
* 🌐 **Demo Online:** _(link da Vercel depois do deploy)_
* ⚙️ **API:** _(link do Render depois do deploy)_, com status em `/api/status`
* 📖 **Modelos UML:** [docs/diagramas.md](docs/diagramas.md) e [docs/historias-de-usuario.md](docs/historias-de-usuario.md)

**Contas de teste** (criadas automaticamente quando o banco está vazio, senha `123456`): `cliente`, `empresa` (Locadora Rota Livre) e `banco` (Banco Horizonte).

---

## 📝 Sobre o Projeto
O sistema apoia a gestão de aluguéis de automóveis. Só pode ser usado depois de cadastro e login. **Clientes** introduzem, modificam, consultam e cancelam pedidos. **Agentes** (empresas e bancos) modificam e avaliam os pedidos do ponto de vista financeiro; com parecer positivo, o pedido pode virar **contrato**, e o automóvel fica registrado em nome do cliente, da empresa ou do banco. O aluguel pode ainda estar associado a um **contrato de crédito** concedido por um dos bancos agentes.

O sistema tem dois subsistemas: a **gestão de pedidos e contratos** (API Spring Boot) e a **construção dinâmica das páginas web** (React + Vite).

---

## ✨ Funcionalidades Principais
- 🔐 **Autenticação:** cadastro de cliente e login com usuário e senha (BCrypt + token JWT); cada papel só acessa o que pode.
- 👤 **CRUD de cliente:** RG, CPF, nome, endereço, profissão e até 3 rendimentos (entidade empregadora + valor). O cliente mantém o próprio cadastro; o agente lista, edita e exclui clientes.
- 📝 **Pedidos de aluguel:** criar, consultar (com status), modificar enquanto pendente e cancelar. Não deixa reservar um carro já reservado nas mesmas datas.
- 🏦 **Análise pelos agentes:** aprovar ou reprovar com parecer, vendo a renda do cliente e o valor total do pedido.
- 📄 **Contratos:** execução do contrato de pedidos aprovados, com proprietário do carro (cliente/empresa/banco) e crédito bancário opcional.
- 🚘 **Frota:** automóveis com matrícula, ano, marca, modelo, placa e diária; agentes cadastram e removem.

---

## 🛠 Tecnologias Utilizadas

### 💻 Front-end
* **Biblioteca:** React 19 + React Router 7
* **Linguagem:** JavaScript (ES2022+)
* **Estilização:** CSS puro
* **Estado:** Context API (sessão do usuário)
* **Build Tool:** Vite 8

### 🖥️ Back-end
* **Linguagem:** Java 25 (JDK 25)
* **Framework:** Spring Boot 4.0.9 (Web MVC, Data JPA, Validation, Security, OAuth2 Resource Server)
* **Build e dependências:** Apache Maven (Maven Wrapper)
* **Banco de Dados:** Supabase (PostgreSQL); H2 em memória para desenvolvimento e testes
* **ORM:** Hibernate / JPA
* **Autenticação:** Spring Security com JWT (HS256) e senhas com BCrypt

### ⚙️ Infraestrutura
* **Front-end:** Vercel · **API:** Render (Docker) · **Banco:** Supabase

---

## 🏗 Arquitetura

Arquitetura **MVC** em camadas, separada em dois subsistemas:

| Camada | Pacote / pasta | Papel |
| :-- | :-- | :-- |
| View | `frontend/src/pages`, `components` | Telas React que montam as páginas dinamicamente |
| Controller | `controller` | Endpoints REST, recebem DTOs validados |
| Service | `service` | Regras de negócio (permissões, conflito de datas, fluxo do pedido) |
| Model | `model` | Entidades JPA com o comportamento do domínio (`Pedido.avaliar`, `Pedido.cancelar`...) |
| Repository | `repository` | Acesso ao PostgreSQL com Spring Data JPA |
| DTO | `dto` | Objetos de entrada/saída da API (records) |
| Security / Config | `security`, `config` | JWT, CORS, regras de acesso por papel e dados iniciais |

**Fluxo do pedido:** `PENDENTE` → (agente avalia) `APROVADO` ou `REPROVADO` → (agente executa contrato) `CONTRATADO`. O cliente pode cancelar enquanto está `PENDENTE` ou `APROVADO`.

### Endpoints

| Método | Rota | Quem |
| :-- | :-- | :-- |
| POST | `/api/auth/login` | público |
| GET | `/api/auth/me` | logado |
| POST | `/api/clientes` | público (cadastro) |
| GET | `/api/clientes` | agente |
| GET / PUT / DELETE | `/api/clientes/{id}` | o próprio cliente ou agente |
| GET | `/api/automoveis` | logado |
| POST / DELETE | `/api/automoveis[/{id}]` | agente |
| GET / POST | `/api/pedidos` | logado / cliente |
| GET / PUT | `/api/pedidos/{id}` | dono ou agente |
| PATCH | `/api/pedidos/{id}/cancelar` | cliente dono |
| PATCH | `/api/pedidos/{id}/avaliacao` | agente |
| POST | `/api/pedidos/{id}/contrato` | agente |
| GET | `/api/agentes/bancos` | agente |

---

## 📐 Modelagem (UML)

Todos os diagramas (com as versões) estão em **[docs/diagramas.md](docs/diagramas.md)**:

- Diagrama de Casos de Uso
- [Histórias de Usuário](docs/historias-de-usuario.md)
- Diagrama de Classes, versão 1 (inicial) e versão 2 (final, alinhada ao código), com a lista do que mudou
- Diagrama de Pacotes (visão lógica)

---

## 🔧 Instalação e Execução

### Pré-requisitos
* Java JDK **25**
* Node.js 20+ e npm

### 🔑 Variáveis de Ambiente

#### 1 Back-end (Spring Boot)
Sem nenhuma variável a API sobe com H2 em memória e os dados de teste.

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `PORT` | Porta da API | `8080` |
| `DB_URL` | JDBC do Supabase (Session pooler) | `jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres` |
| `DB_USER` | Usuário do banco | `postgres.abcdefghijk` |
| `DB_PASSWORD` | Senha do banco | `sua-senha` |
| `JWT_SECRET` | Segredo dos tokens (32+ caracteres) | `uma-frase-bem-grande...` |
| `CORS_ORIGENS` | Origens liberadas | `http://localhost:5173,https://*.vercel.app` |

#### 2 Front-end (React, Vite)

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `VITE_API_URL` | URL da API | `http://localhost:8080` |

### ⚡ Como Executar

#### Terminal 1: Back-end
```bash
cd backend
./mvnw spring-boot:run
```
API em **http://localhost:8080**.

#### Terminal 2: Front-end
```bash
cd frontend
npm install
npm run dev
```
Site em **http://localhost:5173**.

### 💾 Banco de Dados (Supabase)
As tabelas são criadas pelo Hibernate (`ddl-auto=update`) na primeira vez que a API conecta, e os dados de teste são inseridos se o banco estiver vazio.

---

## 🚀 Deploy

1. **Supabase:** crie um projeto e pegue a connection string em **Connect → Session pooler**.
2. **Render (API):** New → Web Service → este repositório, Language **Docker**, Dockerfile Path `./aluguel-api.Dockerfile`, plano Free, health check `/api/status` e as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.
3. **Vercel (front):** importe o repositório com Root Directory `TRABALHO/TRABALHO PRÁTICO 2/frontend`, preset Vite e a variável `VITE_API_URL` com a URL do Render.

---

## 📂 Estrutura de Pastas

```
TRABALHO PRÁTICO 2/
├── README.md
├── docs/                       # 📐 Diagramas UML e histórias de usuário
├── backend/                    # ☕ API Spring Boot (MVC)
│   ├── Dockerfile
│   ├── .env.example
│   ├── pom.xml
│   └── src/main/java/br/pucminas/aluguel/
│       ├── controller/         # 🎮 Endpoints REST
│       ├── service/            # ⚙️ Regras de negócio
│       ├── repository/         # 🗄️ Spring Data JPA
│       ├── model/              # 🧬 Entidades JPA
│       ├── dto/                # ✉️ Records de entrada/saída
│       ├── security/           # 🛡️ Token JWT e usuário logado
│       ├── config/             # 🔧 Segurança, JWT, CORS e dados iniciais
│       └── exception/          # 💥 Tratamento de erros
└── frontend/                   # ⚛️ React + Vite
    ├── public/carro-esportivo.jpg
    └── src/
        ├── pages/              # 📄 Login, Cadastro, Pedidos, Frota, Clientes, Meu cadastro
        ├── components/         # 🧱 Formulário de cliente, tela com foto
        ├── context/            # 🔐 Sessão (AuthContext)
        ├── services/           # 🔌 Chamadas HTTP
        └── utils/              # 🛠️ Formatação
```
O `aluguel-api.Dockerfile` fica na raiz do repositório (o Render não aceita espaço/acento no Root Directory).

---

## 🧪 Testes

```bash
cd backend
./mvnw test
```
Testes de integração com MockMvc: login, acesso negado sem token e por papel, limite de 3 rendimentos, CRUD do cliente e o fluxo completo pedido → conflito de datas → modificação → aprovação → contrato com crédito.

---

## 🔗 Documentações utilizadas
* [Spring Boot](https://docs.spring.io/spring-boot/index.html)
* [Spring Security: OAuth2 Resource Server / JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
* [Supabase: conectar ao Postgres](https://supabase.com/docs/guides/database/connecting-to-postgres)
* [React](https://react.dev/) · [Vite](https://vite.dev/)
* Foto do login: [Porsche 911 por Martin Katler no Unsplash](https://unsplash.com/photos/-m8sosPhg4o) (Licença Unsplash)

---

## 👥 Autores

| 👤 Nome | :octocat: GitHub |
|---------|-----------------|
| Lucas Silva Borges | [LucasSilvaBorges02](https://github.com/LucasSilvaBorges02) |

---

## 🙏 Agradecimentos
* [**Engenharia de Software PUC Minas**](https://www.instagram.com/engsoftwarepucminas/)
* [**Prof. Dr. João Paulo Aramuni**](https://github.com/joaopauloaramuni)
