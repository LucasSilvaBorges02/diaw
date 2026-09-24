package com.example.CandidatosTSE.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.CandidatosTSE.model.Candidato;
import com.example.CandidatosTSE.service.CandidatosTseService;

@Controller
public class CandidatosTseController {

    private final CandidatosTseService candidatosService;

    public CandidatosTseController(CandidatosTseService candidatosService) {
        this.candidatosService = candidatosService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String cargo,
                        @RequestParam(required = false) String partido,
                        @RequestParam(required = false) String texto,
                        Model model) {
        List<Candidato> candidatos = candidatosService.filtrar(cargo, partido, texto);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("total", candidatos.size());
        model.addAttribute("cargos", candidatosService.listarCargos());
        model.addAttribute("partidos", candidatosService.listarPartidos());
        model.addAttribute("cargoSelecionado", cargo == null ? "" : cargo);
        model.addAttribute("partidoSelecionado", partido == null ? "" : partido);
        model.addAttribute("textoSelecionado", texto == null ? "" : texto);
        return "index";
    }
}