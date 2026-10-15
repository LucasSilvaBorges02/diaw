package br.pucminas.diaw.portfolio.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.diaw.portfolio.model.Experiencia;
import br.pucminas.diaw.portfolio.model.Projeto;
import br.pucminas.diaw.portfolio.service.PortfolioService;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("status", "ok");
    }

    @GetMapping("/projetos")
    public List<Projeto> listarProjetos() {
        return service.listarProjetos();
    }

    @GetMapping("/projetos/{id}")
    public Projeto buscarProjeto(@PathVariable String id) {
        return service.buscarProjeto(id);
    }

    @GetMapping("/experiencias")
    public List<Experiencia> listarExperiencias() {
        return service.listarExperiencias();
    }
}
