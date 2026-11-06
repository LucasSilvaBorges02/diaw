package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(Long id, ClienteResumo cliente, AutomovelResponse automovel, LocalDate dataInicio,
        LocalDate dataFim, long dias, BigDecimal valorTotal, StatusPedido status, String parecer,
        String agenteAvaliador, LocalDateTime criadoEm, ContratoResponse contrato) {

    /** O agente precisa ver a renda para fazer a análise financeira. */
    public record ClienteResumo(Long id, String nome, String cpf, String profissao, BigDecimal rendaTotal) {
    }

    public static PedidoResponse de(Pedido pedido) {
        var cliente = pedido.getCliente();
        return new PedidoResponse(pedido.getId(),
                new ClienteResumo(cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getProfissao(),
                        cliente.getRendaTotal()),
                AutomovelResponse.de(pedido.getAutomovel()), pedido.getDataInicio(), pedido.getDataFim(),
                pedido.getDias(), pedido.getValorTotal(), pedido.getStatus(), pedido.getParecer(),
                pedido.getAgenteAvaliador() == null ? null : pedido.getAgenteAvaliador().getNome(),
                pedido.getCriadoEm(), pedido.getContrato() == null ? null : ContratoResponse.de(pedido.getContrato()));
    }
}
