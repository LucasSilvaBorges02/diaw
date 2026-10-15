package br.pucminas.diaw.portfolio.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.diaw.portfolio.exception.ProjetoNaoEncontradoException;
import br.pucminas.diaw.portfolio.model.Experiencia;
import br.pucminas.diaw.portfolio.model.Projeto;
import br.pucminas.diaw.portfolio.repository.PortfolioRepository;

@Service
public class PortfolioService {

    private final PortfolioRepository repository;

    public PortfolioService(PortfolioRepository repository) {
        this.repository = repository;
    }

    /** Projetos do mais antigo ao mais recente, como pede a timeline. */
    public List<Projeto> listarProjetos() {
        return repository.listarProjetos().stream()
                .sorted(Comparator.comparing(Projeto::data))
                .toList();
    }

    public Projeto buscarProjeto(String id) {
        return repository.listarProjetos().stream()
                .filter(projeto -> projeto.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }

    public List<Experiencia> listarExperiencias() {
        return repository.listarExperiencias();
    }
}
