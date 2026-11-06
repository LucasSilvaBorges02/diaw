package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.AvaliacaoRequest;
import br.pucminas.aluguel.dto.ContratoRequest;
import br.pucminas.aluguel.dto.PedidoRequest;
import br.pucminas.aluguel.dto.PedidoResponse;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody @Valid PedidoRequest dados, @AuthenticationPrincipal Jwt jwt) {
        return service.criar(dados, UsuarioLogado.de(jwt));
    }

    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return service.listar(UsuarioLogado.de(jwt));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.buscar(id, UsuarioLogado.de(jwt));
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @RequestBody @Valid PedidoRequest dados,
            @AuthenticationPrincipal Jwt jwt) {
        return service.modificar(id, dados, UsuarioLogado.de(jwt));
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.cancelar(id, UsuarioLogado.de(jwt));
    }

    @PatchMapping("/{id}/avaliacao")
    public PedidoResponse avaliar(@PathVariable Long id, @RequestBody @Valid AvaliacaoRequest dados,
            @AuthenticationPrincipal Jwt jwt) {
        return service.avaliar(id, dados, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/contrato")
    public PedidoResponse executarContrato(@PathVariable Long id, @RequestBody @Valid ContratoRequest dados,
            @AuthenticationPrincipal Jwt jwt) {
        return service.executarContrato(id, dados, UsuarioLogado.de(jwt));
    }
}
