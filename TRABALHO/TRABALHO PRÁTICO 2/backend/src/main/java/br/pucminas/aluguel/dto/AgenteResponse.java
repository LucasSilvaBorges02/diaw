package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.TipoAgente;

public record AgenteResponse(Long id, String nome, String cnpj, TipoAgente tipo) {

    public static AgenteResponse de(Agente agente) {
        return new AgenteResponse(agente.getId(), agente.getNome(), agente.getCnpj(), agente.getTipo());
    }
}
