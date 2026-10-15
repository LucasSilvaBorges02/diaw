package br.pucminas.diaw.portfolio;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioApiApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void statusRespondeOk() throws Exception {
        mvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void listaProjetosComoArray() throws Exception {
        mvc.perform(get("/api/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void projetoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/projetos/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void listaExperienciasComTextosBilingues() throws Exception {
        mvc.perform(get("/api/experiencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empresa").value("PUC Minas"))
                .andExpect(jsonPath("$[0].cargo.pt").exists())
                .andExpect(jsonPath("$[0].cargo.en").exists());
    }

    @Test
    void corsLiberaOFrontEndLocal() throws Exception {
        mvc.perform(get("/api/projetos").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
