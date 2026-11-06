package br.pucminas.aluguel.config;

import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.model.Cliente;
import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.Rendimento;
import br.pucminas.aluguel.model.TipoAgente;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Popula o banco vazio com agentes, um cliente e a frota, para dar pra testar logo de cara. */
@Component
@ConditionalOnProperty(name = "app.seed", havingValue = "true")
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);
    private static final String SENHA_DEMO = "123456";

    private final UsuarioRepository usuarios;
    private final AutomovelRepository automoveis;
    private final PedidoRepository pedidos;
    private final PasswordEncoder encoder;

    public DadosIniciais(UsuarioRepository usuarios, AutomovelRepository automoveis, PedidoRepository pedidos,
            PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.automoveis = automoveis;
        this.pedidos = pedidos;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        usuarios.save(agente("Locadora Rota Livre", "empresa", "12.345.678/0001-90", TipoAgente.EMPRESA));
        usuarios.save(agente("Banco Horizonte", "banco", "98.765.432/0001-10", TipoAgente.BANCO));

        var cliente = new Cliente();
        cliente.setNome("Ana Ribeiro");
        cliente.setLogin("cliente");
        cliente.setSenha(encoder.encode(SENHA_DEMO));
        cliente.setRg("MG-12.345.678");
        cliente.setCpf("12345678909");
        cliente.setEndereco("Rua da Bahia, 1000 - Belo Horizonte/MG");
        cliente.setProfissao("Analista de sistemas");
        cliente.substituirRendimentos(List.of(
                new Rendimento("Tech Minas Ltda", new BigDecimal("6500.00")),
                new Rendimento("Freelas", new BigDecimal("1200.00"))));
        usuarios.save(cliente);

        List<Automovel> frota = automoveis.saveAll(List.of(
                carro("90000000001", 2024, "Porsche", "911 Carrera", "RTL1A11", "1890.00"),
                carro("90000000002", 2023, "BMW", "M4 Competition", "RTL2B22", "1490.00"),
                carro("90000000003", 2024, "Ford", "Mustang GT", "RTL3C33", "1050.00"),
                carro("90000000004", 2023, "Chevrolet", "Camaro SS", "RTL4D44", "990.00"),
                carro("90000000005", 2024, "Jeep", "Compass Longitude", "RTL5E55", "260.00"),
                carro("90000000006", 2023, "Honda", "Civic Touring", "RTL6F66", "240.00"),
                carro("90000000007", 2024, "Toyota", "Corolla XEi", "RTL7G77", "220.00"),
                carro("90000000008", 2024, "Volkswagen", "Polo TSI", "RTL8H88", "160.00")));

        LocalDate inicio = LocalDate.now().plusDays(7);
        pedidos.save(new Pedido(cliente, frota.get(2), inicio, inicio.plusDays(3)));
        log.info("Banco vazio: criei os usuários de demonstração (empresa, banco e cliente, senha {}) e a frota", SENHA_DEMO);
    }

    private Agente agente(String nome, String login, String cnpj, TipoAgente tipo) {
        var agente = new Agente();
        agente.setNome(nome);
        agente.setLogin(login);
        agente.setSenha(encoder.encode(SENHA_DEMO));
        agente.setCnpj(cnpj);
        agente.setTipo(tipo);
        return agente;
    }

    private Automovel carro(String matricula, int ano, String marca, String modelo, String placa, String diaria) {
        var automovel = new Automovel();
        automovel.setMatricula(matricula);
        automovel.setAno(ano);
        automovel.setMarca(marca);
        automovel.setModelo(modelo);
        automovel.setPlaca(placa);
        automovel.setValorDiaria(new BigDecimal(diaria));
        return automovel;
    }
}
