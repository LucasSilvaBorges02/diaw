# Horizonte — autenticação e cadastro de usuários

Aplicação web em **Spring Boot + Thymeleaf** com login, cadastro e recuperação de senha por email.
Atividade da disciplina **DIAW — Desenvolvimento e Integração de Aplicações Web** (PUC Minas).

A identidade visual é própria: céu em gradiente animado (violeta → magenta → âmbar), silhueta
de serra no rodapé e formulário flutuante em vidro. Layout responsivo e com `prefers-reduced-motion`
respeitado.

---

## Tecnologias

| Recurso | Uso |
|---|---|
| Spring Boot 3.3 | base da aplicação |
| Spring Security 6 | autenticação, sessão e proteção de rotas |
| Spring Data JPA + H2 | persistência dos usuários |
| Thymeleaf | telas HTML integradas ao backend |
| Bean Validation | validação dos formulários |
| Spring Boot Mail | envio do link de recuperação |
| BCrypt | hash das senhas |

---

## Como executar

Pré-requisitos: **JDK 17+** e **Maven 3.9+** (ou abrir o projeto direto na IDE).

```bash
mvn spring-boot:run
```

Depois abra <http://localhost:8080/login>.

Usuário de demonstração criado automaticamente na primeira execução:

```
login: aluno
senha: horizonte2026
```

Para desligar esse usuário de teste, use `app.seed-demo=false` no `application.properties`.

Banco de dados: H2 em arquivo (`./data/horizonte`), console em <http://localhost:8080/h2-console>
(JDBC URL `jdbc:h2:file:./data/horizonte`, usuário `sa`, senha vazia).

---

## Endpoints

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| GET | `/login` | Tela de login | público |
| POST | `/login` | Autenticação (processada pelo Spring Security) | público |
| GET | `/register` | Tela de cadastro | público |
| POST | `/register` | Processa o cadastro | público |
| GET | `/recoverpassword` | Tela de recuperação de senha | público |
| POST | `/recoverpassword` | Gera o token e envia o email | público |
| GET | `/resetpassword?token=...` | Tela de nova senha | público |
| POST | `/resetpassword` | Salva a nova senha | público |
| GET | `/home` | Área protegida | autenticado |
| POST | `/logout` | Encerra a sessão | autenticado |
| GET | `/` | Redireciona para `/home` | — |

---

## Segurança

- Senhas gravadas **apenas como hash BCrypt** (salt aleatório por senha); nada em texto puro.
- Login aceita **nome de usuário ou email** (`AutenticacaoService`).
- Rotas fora da lista pública exigem sessão autenticada; sem sessão, o Spring Security redireciona para `/login`.
- CSRF ativo em todos os formulários (o Thymeleaf insere o token automaticamente nos `form` com `th:action`).
- Token de recuperação: UUID, validade de 30 minutos, **uso único**, e tokens antigos do mesmo usuário são invalidados a cada novo pedido.
- `/recoverpassword` responde **sempre a mesma mensagem**, exista ou não a conta — evita descobrir emails cadastrados por tentativa.

### Validações do cadastro

- Campos obrigatórios não podem ficar vazios.
- Email precisa ter formato válido.
- Nome de usuário: 3–40 caracteres, apenas letras, números, `.`, `-` e `_`.
- Senha: mínimo de 8 caracteres, com letras **e** números.
- Senha e confirmação precisam ser iguais.
- Nome de usuário e email não podem estar duplicados.

---

## Configuração do email

Por padrão o envio vem **desligado** e o link de recuperação é impresso no console — dá para
testar o fluxo inteiro sem configurar nada.

Para enviar de verdade (exemplo com Gmail), defina as variáveis de ambiente antes de rodar:

```bash
export MAIL_ENABLED=true
export MAIL_USER=seu.email@gmail.com
export MAIL_PASS=sua-senha-de-app       # senha de app, não a senha da conta
mvn spring-boot:run
```

No Windows (PowerShell):

```powershell
$env:MAIL_ENABLED="true"
$env:MAIL_USER="seu.email@gmail.com"
$env:MAIL_PASS="sua-senha-de-app"
mvn spring-boot:run
```

Variáveis disponíveis: `MAIL_ENABLED`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASS`,
`APP_BASE_URL`, `DB_USER`, `DB_PASS`.

> Nenhuma credencial fica no código: o `application.properties` só lê variáveis de ambiente,
> e `.env` está no `.gitignore`.

---

## Estrutura

```
src/main/java/br/pucminas/diaw/auth/
├── HorizonteApplication.java
├── config/       SecurityConfig, DadosIniciais
├── controller/   AuthController, HomeController
├── dto/          CadastroForm, NovaSenhaForm
├── model/        Usuario, TokenRecuperacao
├── repository/   UsuarioRepository, TokenRecuperacaoRepository
└── service/      UsuarioService, AutenticacaoService, EmailService, RecuperacaoSenhaService

src/main/resources/
├── application.properties
├── static/css/horizonte.css
└── templates/
    ├── fragments/layout.html, fragments/marca.html
    ├── login.html
    ├── register.html
    ├── recoverpassword.html
    ├── resetpassword.html
    └── home.html
```

---

## Autores

Lucas e Arthur Monserrat — Engenharia de Software, PUC Minas.
