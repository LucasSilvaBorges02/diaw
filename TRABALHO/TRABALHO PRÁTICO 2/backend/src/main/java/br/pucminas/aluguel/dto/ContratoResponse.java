package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Contrato;
import br.pucminas.aluguel.model.TipoProprietario;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContratoResponse(Long id, TipoProprietario tipoProprietario, String proprietario, String agente,
        String bancoCredito, BigDecimal valorCredito, LocalDateTime assinadoEm) {

    public static ContratoResponse de(Contrato contrato) {
        var credito = contrato.getCredito();
        return new ContratoResponse(contrato.getId(), contrato.getTipoProprietario(),
                contrato.getProprietario().getNome(), contrato.getAgente().getNome(),
                credito == null ? null : credito.getBanco().getNome(),
                credito == null ? null : credito.getValor(), contrato.getAssinadoEm());
    }
}
