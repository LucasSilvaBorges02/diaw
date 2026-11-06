package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AutomovelRequest;
import br.pucminas.aluguel.dto.AutomovelResponse;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomovelService {

    private final AutomovelRepository automoveis;
    private final PedidoRepository pedidos;

    public AutomovelService(AutomovelRepository automoveis, PedidoRepository pedidos) {
        this.automoveis = automoveis;
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar() {
        return automoveis.findAllByOrderByMarcaAscModeloAsc().stream().map(AutomovelResponse::de).toList();
    }

    @Transactional
    public AutomovelResponse cadastrar(AutomovelRequest dados) {
        String placa = dados.placa().trim().toUpperCase(Locale.ROOT).replace("-", "");
        if (automoveis.existsByPlaca(placa)) {
            throw new IllegalStateException("Já existe um automóvel com essa placa");
        }
        if (automoveis.existsByMatricula(dados.matricula().trim())) {
            throw new IllegalStateException("Já existe um automóvel com essa matrícula");
        }
        var automovel = new Automovel();
        automovel.setMatricula(dados.matricula().trim());
        automovel.setAno(dados.ano());
        automovel.setMarca(dados.marca().trim());
        automovel.setModelo(dados.modelo().trim());
        automovel.setPlaca(placa);
        automovel.setValorDiaria(dados.valorDiaria());
        return AutomovelResponse.de(automoveis.save(automovel));
    }

    @Transactional
    public void excluir(Long id) {
        var automovel = automoveis.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
        if (pedidos.existsByAutomovelId(id)) {
            throw new IllegalStateException("Esse automóvel já tem pedidos e não pode ser removido");
        }
        automoveis.delete(automovel);
    }
}
