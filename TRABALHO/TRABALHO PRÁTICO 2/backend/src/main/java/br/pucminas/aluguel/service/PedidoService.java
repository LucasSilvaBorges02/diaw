package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AvaliacaoRequest;
import br.pucminas.aluguel.dto.ContratoRequest;
import br.pucminas.aluguel.dto.PedidoRequest;
import br.pucminas.aluguel.dto.PedidoResponse;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.model.Contrato;
import br.pucminas.aluguel.model.ContratoCredito;
import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusPedido;
import br.pucminas.aluguel.model.TipoAgente;
import br.pucminas.aluguel.model.Usuario;
import br.pucminas.aluguel.repository.AgenteRepository;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.ClienteRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private static final EnumSet<StatusPedido> ATIVOS =
            EnumSet.of(StatusPedido.PENDENTE, StatusPedido.APROVADO, StatusPedido.CONTRATADO);

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final AutomovelRepository automoveis;
    private final AgenteRepository agentes;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, AutomovelRepository automoveis,
            AgenteRepository agentes) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.automoveis = automoveis;
        this.agentes = agentes;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest dados, UsuarioLogado logado) {
        var cliente = clientes.findById(logado.id())
                .orElseThrow(() -> new AccessDeniedException("Só clientes podem fazer pedidos"));
        var automovel = buscarAutomovel(dados.automovelId());
        validarDatas(dados, automovel, -1L);
        return PedidoResponse.de(pedidos.save(new Pedido(cliente, automovel, dados.dataInicio(), dados.dataFim())));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(UsuarioLogado logado) {
        var lista = logado.isAgente() ? pedidos.listarTodos() : pedidos.listarDoCliente(logado.id());
        return lista.stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, UsuarioLogado logado) {
        return PedidoResponse.de(encontrarVisivel(id, logado));
    }

    /** Clientes mudam os próprios pedidos; agentes podem mudar qualquer um. Só enquanto está pendente. */
    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest dados, UsuarioLogado logado) {
        var pedido = encontrarVisivel(id, logado);
        if (!pedido.getStatus().podeSerModificado()) {
            throw new IllegalStateException("Só pedidos pendentes podem ser modificados");
        }
        var automovel = buscarAutomovel(dados.automovelId());
        validarDatas(dados, automovel, pedido.getId());
        pedido.alterar(automovel, dados.dataInicio(), dados.dataFim());
        return PedidoResponse.de(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, UsuarioLogado logado) {
        var pedido = encontrarVisivel(id, logado);
        pedido.cancelar();
        return PedidoResponse.de(pedido);
    }

    /** Análise financeira feita pelo agente: aprova ou reprova com um parecer. */
    @Transactional
    public PedidoResponse avaliar(Long id, AvaliacaoRequest dados, UsuarioLogado logado) {
        var pedido = encontrar(id);
        pedido.avaliar(buscarAgente(logado.id()), dados.aprovado(), dados.parecer());
        return PedidoResponse.de(pedido);
    }

    /** Pedido aprovado vira contrato; o carro fica registrado em nome do cliente, da empresa ou do banco. */
    @Transactional
    public PedidoResponse executarContrato(Long id, ContratoRequest dados, UsuarioLogado logado) {
        var pedido = encontrar(id);
        var agente = buscarAgente(logado.id());
        Agente banco = dados.bancoId() == null ? null : buscarBanco(dados.bancoId());

        Usuario proprietario = switch (dados.tipoProprietario()) {
            case CLIENTE -> pedido.getCliente();
            case EMPRESA -> {
                if (agente.getTipo() != TipoAgente.EMPRESA) {
                    throw new IllegalArgumentException("Só uma empresa pode registrar o carro em nome da empresa");
                }
                yield agente;
            }
            case BANCO -> {
                if (banco == null) {
                    throw new IllegalArgumentException("Escolha o banco que concede o crédito");
                }
                yield banco;
            }
        };

        ContratoCredito credito = null;
        if (banco != null) {
            var valor = dados.valorCredito() != null ? dados.valorCredito() : pedido.getValorTotal();
            credito = new ContratoCredito(banco, valor);
        }
        pedido.registrarContrato(new Contrato(pedido, agente, dados.tipoProprietario(), proprietario, credito));
        pedidos.flush();
        return PedidoResponse.de(pedido);
    }

    private void validarDatas(PedidoRequest dados, Automovel automovel, Long ignorarId) {
        if (dados.dataInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A retirada não pode ser numa data que já passou");
        }
        if (!dados.dataFim().isAfter(dados.dataInicio())) {
            throw new IllegalArgumentException("A data de devolução precisa ser depois da retirada");
        }
        if (pedidos.existeConflito(automovel.getId(), dados.dataInicio(), dados.dataFim(), ignorarId, ATIVOS)) {
            throw new IllegalStateException("Esse carro já está reservado nessas datas");
        }
    }

    private Pedido encontrarVisivel(Long id, UsuarioLogado logado) {
        var pedido = encontrar(id);
        logado.exigirDonoOuAgente(pedido.getCliente().getId());
        return pedido;
    }

    private Pedido encontrar(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
    }

    private Automovel buscarAutomovel(Long id) {
        return automoveis.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
    }

    private Agente buscarAgente(Long id) {
        return agentes.findById(id).orElseThrow(() -> new AccessDeniedException("Só agentes podem fazer isso"));
    }

    private Agente buscarBanco(Long id) {
        var banco = agentes.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Banco não encontrado"));
        if (!banco.isBanco()) {
            throw new IllegalArgumentException("O agente escolhido não é um banco");
        }
        return banco;
    }
}
