package br.pucminas.aluguel.dto;

import java.util.Map;

public record ErroResponse(String erro, Map<String, String> campos) {

    public ErroResponse(String erro) {
        this(erro, Map.of());
    }
}
