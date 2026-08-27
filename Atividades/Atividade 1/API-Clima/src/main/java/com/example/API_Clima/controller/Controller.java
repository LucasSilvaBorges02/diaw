package com.example.API_Clima.controller;

import com.example.API_Clima.dto.ClimaDTO;
import com.example.API_Clima.service.Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping("/climaBH")
    public ClimaDTO climaBeloHorizonte() {
        return service.buscarClima();
    }
}