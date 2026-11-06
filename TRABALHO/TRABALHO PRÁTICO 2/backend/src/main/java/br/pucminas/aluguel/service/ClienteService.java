package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.ClienteRequest;
import br.pucminas.aluguel.dto.ClienteResponse;
import br.pucminas.aluguel.dto.RendimentoDto;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Cliente;
import br.pucminas.aluguel.repository.ClienteRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.repository.UsuarioRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final PasswordEncoder encoder;

    public ClienteService(ClienteRepository clientes, UsuarioRepository usuarios, PedidoRepository pedidos,
            PasswordEncoder encoder) {
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.encoder = encoder;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest dados) {
        if (dados.senha() == null || dados.senha().isBlank()) {
            throw new IllegalArgumentException("Informe uma senha");
        }
        String login = dados.login().trim();
        String cpf = somenteDigitos(dados.cpf());
        if (usuarios.existsByLogin(login)) {
            throw new IllegalStateException("Esse usuário já está em uso");
        }
        if (clientes.existsByCpf(cpf)) {
            throw new IllegalStateException("Já existe um cliente com esse CPF");
        }
        var cliente = new Cliente();
        preencher(cliente, dados, login, cpf);
        return ClienteResponse.de(clientes.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAllByOrderByNomeAsc().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, UsuarioLogado logado) {
        logado.exigirDonoOuAgente(id);
        return ClienteResponse.de(encontrar(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest dados, UsuarioLogado logado) {
        logado.exigirDonoOuAgente(id);
        var cliente = encontrar(id);
        String login = dados.login().trim();
        String cpf = somenteDigitos(dados.cpf());
        if (usuarios.existsByLoginAndIdNot(login, id)) {
            throw new IllegalStateException("Esse usuário já está em uso");
        }
        if (clientes.existsByCpfAndIdNot(cpf, id)) {
            throw new IllegalStateException("Já existe um cliente com esse CPF");
        }
        preencher(cliente, dados, login, cpf);
        return ClienteResponse.de(cliente);
    }

    @Transactional
    public void excluir(Long id, UsuarioLogado logado) {
        logado.exigirDonoOuAgente(id);
        var cliente = encontrar(id);
        pedidos.deleteAll(pedidos.findByClienteId(id));
        pedidos.flush();
        clientes.delete(cliente);
    }

    private void preencher(Cliente cliente, ClienteRequest dados, String login, String cpf) {
        cliente.setNome(dados.nome().trim());
        cliente.setLogin(login);
        if (dados.senha() != null && !dados.senha().isBlank()) {
            cliente.setSenha(encoder.encode(dados.senha()));
        }
        cliente.setRg(dados.rg().trim());
        cliente.setCpf(cpf);
        cliente.setEndereco(dados.endereco().trim());
        cliente.setProfissao(dados.profissao() == null ? null : dados.profissao().trim());
        var rendimentos = dados.rendimentos() == null ? List.<RendimentoDto>of() : dados.rendimentos();
        cliente.substituirRendimentos(rendimentos.stream().map(RendimentoDto::paraEntidade).toList());
    }

    private Cliente encontrar(Long id) {
        return clientes.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    private static String somenteDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }
}
