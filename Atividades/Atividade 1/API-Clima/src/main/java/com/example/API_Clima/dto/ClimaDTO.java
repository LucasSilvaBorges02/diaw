package com.example.API_Clima.dto;

public record ClimaDTO(
        String cidade,
        double latitude,
        double longitude,
        double temperatura,
        int umidade,
        double velocidadeVento,
        int direcaoVentoGraus,
        String direcaoVento,
        double temperaturaMaxima,
        double temperaturaMinima,
        String descricao,
        String dataHoraConsulta) {
}