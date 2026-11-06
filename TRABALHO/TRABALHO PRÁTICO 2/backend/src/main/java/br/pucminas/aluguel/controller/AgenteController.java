package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.AgenteResponse;
import br.pucminas.aluguel.service.AgenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agentes")
public class AgenteController {

    private final AgenteService service;

    public AgenteController(AgenteService service) {
        this.service = service;
    }

    /** Bancos que podem conceder crédito na hora de fechar o contrato. */
    @GetMapping("/bancos")
    public List<AgenteResponse> bancos() {
        return service.listarBancos();
    }
}
