package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AgenteResponse;
import br.pucminas.aluguel.model.TipoAgente;
import br.pucminas.aluguel.repository.AgenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenteService {

    private final AgenteRepository agentes;

    public AgenteService(AgenteRepository agentes) {
        this.agentes = agentes;
    }

    @Transactional(readOnly = true)
    public List<AgenteResponse> listarBancos() {
        return agentes.findByTipoOrderByNomeAsc(TipoAgente.BANCO).stream().map(AgenteResponse::de).toList();
    }
}
