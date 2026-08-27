package com.example.API_Clima.service;

import com.example.API_Clima.dto.ClimaDTO;
import com.example.API_Clima.dto.OpenMeteoResponse;
import com.example.API_Clima.exception.ClimaException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@org.springframework.stereotype.Service
public class Service {

    private final RestClient restClient;
    private final String cidade;
    private final double latitude;
    private final double longitude;

    public Service(RestClient.Builder builder,
                   @Value("${clima.api.url}") String url,
                   @Value("${clima.cidade}") String cidade,
                   @Value("${clima.latitude}") double latitude,
                   @Value("${clima.longitude}") double longitude) {
        this.restClient = builder.baseUrl(url).build();
        this.cidade = cidade;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ClimaDTO buscarClima() {
        OpenMeteoResponse resposta;

        try {
            resposta = restClient.get()
                    .uri(uri -> uri
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current",
                                    "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m")
                            .queryParam("daily", "temperature_2m_max,temperature_2m_min")
                            .queryParam("timezone", "America/Sao_Paulo")
                            .queryParam("forecast_days", 1)
                            .build())
                    .retrieve()
                    .body(OpenMeteoResponse.class);
        } catch (Exception e) {
            throw new ClimaException("Falha na comunicacao com a API de clima.", e);
        }

        if (resposta == null || resposta.current() == null || resposta.daily() == null) {
            throw new ClimaException("Dados meteorologicos indisponiveis no momento.");
        }

        OpenMeteoResponse.Current atual = resposta.current();

        return new ClimaDTO(
                cidade,
                resposta.latitude(),
                resposta.longitude(),
                atual.temperatura(),
                atual.umidade(),
                atual.velocidadeVento(),
                atual.direcaoVento(),
                converterDirecao(atual.direcaoVento()),
                resposta.daily().maximas().get(0),
                resposta.daily().minimas().get(0),
                descreverTempo(atual.codigoTempo()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    private String converterDirecao(int graus) {
        String[] pontos = { "Norte", "Nordeste", "Leste", "Sudeste",
                            "Sul", "Sudoeste", "Oeste", "Noroeste" };
        return pontos[(int) Math.round(graus / 45.0) % 8];
    }

    private String descreverTempo(int codigo) {
        return switch (codigo) {
            case 0 -> "Ceu limpo";
            case 1 -> "Predominantemente limpo";
            case 2 -> "Parcialmente nublado";
            case 3 -> "Nublado";
            case 45, 48 -> "Nevoa";
            case 51, 53, 55 -> "Garoa";
            case 61, 63, 65 -> "Chuva";
            case 71, 73, 75 -> "Neve";
            case 80, 81, 82 -> "Pancadas de chuva";
            case 95, 96, 99 -> "Tempestade";
            default -> "Condicao nao identificada";
        };
    }
}