package br.pucminas.diaw.portfolio.model;

import java.util.List;

/**
 * Projeto exibido na timeline.
 *
 * @param data mes de referencia no formato AAAA-MM, usado para ordenar a timeline
 * @param imagem caminho da imagem ou GIF servido pelo front-end (ex.: /projetos/meu-projeto.gif), pode ser nulo
 */
public record Projeto(
        String id,
        String data,
        String nome,
        Texto descricao,
        List<String> tecnologias,
        String github,
        String imagem) {
}
