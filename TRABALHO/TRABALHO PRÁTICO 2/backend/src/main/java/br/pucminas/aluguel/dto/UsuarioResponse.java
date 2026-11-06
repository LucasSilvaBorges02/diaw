package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.Papel;
import br.pucminas.aluguel.model.TipoAgente;
import br.pucminas.aluguel.model.Usuario;

public record UsuarioResponse(Long id, String nome, String login, Papel papel, TipoAgente tipoAgente) {

    public static UsuarioResponse de(Usuario usuario) {
        TipoAgente tipo = usuario instanceof Agente agente ? agente.getTipo() : null;
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getPapel(), tipo);
    }
}
