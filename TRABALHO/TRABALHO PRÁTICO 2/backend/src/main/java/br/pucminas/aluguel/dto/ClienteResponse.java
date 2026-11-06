package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Cliente;
import java.math.BigDecimal;
import java.util.List;

public record ClienteResponse(Long id, String nome, String login, String rg, String cpf, String endereco,
        String profissao, List<RendimentoDto> rendimentos, BigDecimal rendaTotal) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getLogin(), cliente.getRg(),
                cliente.getCpf(), cliente.getEndereco(), cliente.getProfissao(),
                cliente.getRendimentos().stream().map(RendimentoDto::de).toList(), cliente.getRendaTotal());
    }
}
