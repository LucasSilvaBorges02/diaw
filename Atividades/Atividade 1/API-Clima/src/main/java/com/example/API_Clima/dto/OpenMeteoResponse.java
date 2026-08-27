package com.example.API_Clima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        Current current,
        Daily daily) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            @JsonProperty("temperature_2m") double temperatura,
            @JsonProperty("relative_humidity_2m") int umidade,
            @JsonProperty("weather_code") int codigoTempo,
            @JsonProperty("wind_speed_10m") double velocidadeVento,
            @JsonProperty("wind_direction_10m") int direcaoVento) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            @JsonProperty("temperature_2m_max") List<Double> maximas,
            @JsonProperty("temperature_2m_min") List<Double> minimas) {
    }
}