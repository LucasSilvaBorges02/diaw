package br.pucminas.diaw.portfolio.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import br.pucminas.diaw.portfolio.model.Experiencia;
import br.pucminas.diaw.portfolio.model.Projeto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Le os dados do portfolio dos arquivos JSON em src/main/resources/dados.
 * Os arquivos sao carregados uma vez, na inicializacao da aplicacao.
 */
@Repository
public class PortfolioRepository {

    private final List<Projeto> projetos;
    private final List<Experiencia> experiencias;

    public PortfolioRepository(JsonMapper jsonMapper) {
        this.projetos = ler(jsonMapper, "dados/projetos.json", new TypeReference<>() {});
        this.experiencias = ler(jsonMapper, "dados/experiencias.json", new TypeReference<>() {});
    }

    public List<Projeto> listarProjetos() {
        return projetos;
    }

    public List<Experiencia> listarExperiencias() {
        return experiencias;
    }

    private static <T> List<T> ler(JsonMapper jsonMapper, String caminho, TypeReference<List<T>> tipo) {
        try (InputStream entrada = new ClassPathResource(caminho).getInputStream()) {
            return List.copyOf(jsonMapper.readValue(entrada, tipo));
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler " + caminho, e);
        }
    }
}
