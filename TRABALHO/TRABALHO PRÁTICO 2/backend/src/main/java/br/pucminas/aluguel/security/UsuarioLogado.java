package br.pucminas.aluguel.security;

import br.pucminas.aluguel.model.Papel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

/** Quem está fazendo a requisição, lido do token JWT. */
public record UsuarioLogado(Long id, Papel papel) {

    public static UsuarioLogado de(Jwt jwt) {
        return new UsuarioLogado(Long.valueOf(jwt.getSubject()), Papel.valueOf(jwt.getClaimAsString("papel")));
    }

    public boolean isAgente() {
        return papel == Papel.AGENTE;
    }

    public boolean isCliente() {
        return papel == Papel.CLIENTE;
    }

    /** Agentes enxergam tudo; clientes, só o que é deles. */
    public void exigirDonoOuAgente(Long donoId) {
        if (!isAgente() && !id.equals(donoId)) {
            throw new AccessDeniedException("Você não tem acesso a este recurso");
        }
    }
}
