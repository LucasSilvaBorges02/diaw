package br.pucminas.aluguel;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AluguelApiApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void statusEhPublico() throws Exception {
        mvc.perform(get("/api/status")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void semLoginNaoAcessaPedidos() throws Exception {
        mvc.perform(get("/api/pedidos")).andExpect(status().isUnauthorized());
    }

    @Test
    void senhaErradaDa401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"cliente\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteNaoListaOutrosClientes() throws Exception {
        String token = login("cliente");
        mvc.perform(get("/api/clientes").header("Authorization", token)).andExpect(status().isForbidden());
    }

    @Test
    void naoAceitaMaisDeTresRendimentos() throws Exception {
        String rendimento = "{\"entidadeEmpregadora\":\"X\",\"valor\":100}";
        String corpo = cliente("muitos", "11122233344",
                "[" + String.join(",", rendimento, rendimento, rendimento, rendimento) + "]");
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.rendimentos").exists());
    }

    @Test
    void fluxoCompletoDoPedidoAteOContrato() throws Exception {
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                .content(cliente("joao", "529.982.247-25", "[{\"entidadeEmpregadora\":\"PUC\",\"valor\":3500}]")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("52998224725"));

        String tokenCliente = login("joao");
        String automoveis = mvc.perform(get("/api/automoveis").header("Authorization", tokenCliente))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Integer carroId = JsonPath.read(automoveis, "$[0].id");

        LocalDate inicio = LocalDate.now().plusDays(60);
        String pedido = mvc.perform(post("/api/pedidos").header("Authorization", tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(pedido(carroId, inicio, inicio.plusDays(2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.dias").value(2))
                .andReturn().getResponse().getContentAsString();
        Integer pedidoId = JsonPath.read(pedido, "$.id");

        // mesmo carro, datas que se cruzam: conflito
        mvc.perform(post("/api/pedidos").header("Authorization", tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(pedido(carroId, inicio.plusDays(1), inicio.plusDays(4))))
                .andExpect(status().isConflict());

        // cliente modifica o próprio pedido enquanto está pendente
        mvc.perform(put("/api/pedidos/" + pedidoId).header("Authorization", tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(pedido(carroId, inicio, inicio.plusDays(5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dias").value(5));

        String tokenEmpresa = login("empresa");
        mvc.perform(patch("/api/pedidos/" + pedidoId + "/avaliacao").header("Authorization", tokenEmpresa)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"aprovado\":true,\"parecer\":\"Renda compatível\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        String bancos = mvc.perform(get("/api/agentes/bancos").header("Authorization", tokenEmpresa))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Integer bancoId = JsonPath.read(bancos, "$[0].id");

        mvc.perform(post("/api/pedidos/" + pedidoId + "/contrato").header("Authorization", tokenEmpresa)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoProprietario\":\"BANCO\",\"bancoId\":" + bancoId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTRATADO"))
                .andExpect(jsonPath("$.contrato.proprietario").value("Banco Horizonte"))
                .andExpect(jsonPath("$.contrato.bancoCredito").value("Banco Horizonte"));

        mvc.perform(patch("/api/pedidos/" + pedidoId + "/cancelar").header("Authorization", tokenCliente))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/pedidos").header("Authorization", tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void clienteEditaEExcluiAPropriaConta() throws Exception {
        String criado = mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                .content(cliente("maria", "11144477735", "[]")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(criado, "$.id");
        String token = login("maria");

        String edicao = cliente("maria", "11144477735", "[]").replace("\"senha\":\"123456\",", "")
                .replace("Estudante", "Engenheira");
        mvc.perform(put("/api/clientes/" + id).header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(edicao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profissao").value("Engenheira"));

        // a senha antiga continua valendo
        login("maria");

        mvc.perform(get("/api/clientes/1").header("Authorization", token)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/clientes/" + id).header("Authorization", token)).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"maria\",\"senha\":\"123456\"}"))
                .andExpect(status().isUnauthorized());
    }

    private String login(String usuario) throws Exception {
        String resposta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"" + usuario + "\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(resposta, "$.token");
    }

    private static String cliente(String login, String cpf, String rendimentos) {
        return """
                {"nome":"Teste %s","login":"%s","senha":"123456","rg":"MG-1","cpf":"%s",
                 "endereco":"Rua A, 1","profissao":"Estudante","rendimentos":%s}"""
                .formatted(login, login, cpf, rendimentos);
    }

    private static String pedido(Integer carroId, LocalDate inicio, LocalDate fim) {
        return "{\"automovelId\":%d,\"dataInicio\":\"%s\",\"dataFim\":\"%s\"}".formatted(carroId, inicio, fim);
    }
}
