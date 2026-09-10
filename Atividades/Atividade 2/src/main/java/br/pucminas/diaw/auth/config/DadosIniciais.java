package br.pucminas.diaw.auth.config;

import br.pucminas.diaw.auth.dto.CadastroForm;
import br.pucminas.diaw.auth.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Cria um usuario de demonstracao na primeira execucao.
 * Desligue com app.seed-demo=false (ou remova esta classe) antes de publicar.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo", havingValue = "true", matchIfMissing = true)
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final UsuarioService usuarioService;

    public DadosIniciais(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.usernameEmUso("aluno")) {
            return;
        }

        CadastroForm demo = new CadastroForm();
        demo.setNome("Aluno Demonstracao");
        demo.setUsername("aluno");
        demo.setEmail("aluno@pucminas.br");
        demo.setSenha("horizonte2026");
        demo.setConfirmacaoSenha("horizonte2026");

        usuarioService.cadastrar(demo);
        log.info("Usuario de demonstracao criado -> login: aluno | senha: horizonte2026");
    }
}
