package br.pucminas.diaw.portfolio.model;

/**
 * Experiencia profissional, estagio, freela, open source, evento ou formacao.
 *
 * @param tipo trabalho | estagio | freela | open-source | evento | academico
 * @param descricao opcional
 */
public record Experiencia(
        String id,
        String tipo,
        String empresa,
        Texto cargo,
        Texto periodo,
        Texto descricao) {
}
