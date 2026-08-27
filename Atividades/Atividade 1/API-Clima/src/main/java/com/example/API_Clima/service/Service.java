package com.example.API_Clima.service;

import com.example.API_Clima.dto.ClimaDTO;
import com.example.API_Clima.dto.GeocodingResponse;
import com.example.API_Clima.dto.OpenMeteoResponse;
import com.example.API_Clima.exception.CidadeNaoEncontradaException;
import com.example.API_Clima.exception.ClimaException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@org.springframework.stereotype.Service
public class Service {

    private final RestClient climaClient;
    private final RestClient geoClient;
    private final String cidadePadrao;
    private final double latitude;
    private final double longitude;

    public Service(RestClient.Builder builder,
                   @Value("${clima.api.url}") String urlClima,
                   @Value("${clima.geocoding.url}") String urlGeocoding,
                   @Value("${clima.cidade}") String cidadePadrao,
                   @Value("${clima.latitude}") double latitude,
                   @Value("${clima.longitude}") double longitude) {
        this.climaClient = builder.clone().baseUrl(urlClima).build();
        this.geoClient = builder.clone().baseUrl(urlGeocoding).build();
        this.cidadePadrao = cidadePadrao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ClimaDTO buscarClima() {
        return consultarPrevisao(cidadePadrao, latitude, longitude);
    }

    public ClimaDTO buscarClimaPorCidade(String nomeCidade) {
        String nome = nomeCidade.replace("-", " ").trim();

        if (nome.isEmpty()) {
            throw new CidadeNaoEncontradaException("Informe o nome de uma cidade.");
        }

        GeocodingResponse geo;

        try {
            geo = geoClient.get()
                    .uri(uri -> uri
                            .queryParam("name", nome)
                            .queryParam("count", 1)
                            .queryParam("language", "pt")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(GeocodingResponse.class);
        } catch (Exception e) {
            throw new ClimaException("Falha na comunicacao com o servico de geocoding.", e);
        }

        if (geo == null || geo.results() == null || geo.results().isEmpty()) {
            throw new CidadeNaoEncontradaException("Cidade nao encontrada: " + nome);
        }

        GeocodingResponse.Resultado local = geo.results().get(0);

        String rotulo = local.admin1() != null
                ? local.name() + " - " + local.admin1()
                : local.name();

        return consultarPrevisao(rotulo, local.latitude(), local.longitude());
    }

    private ClimaDTO consultarPrevisao(String cidade, double lat, double lon) {
        OpenMeteoResponse resposta;

        try {
            resposta = climaClient.get()
                    .uri(uri -> uri
                            .queryParam("latitude", lat)
                            .queryParam("longitude", lon)
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