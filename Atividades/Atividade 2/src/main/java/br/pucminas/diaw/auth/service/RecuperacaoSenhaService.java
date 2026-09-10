package br.pucminas.diaw.auth.service;

import br.pucminas.diaw.auth.model.TokenRecuperacao;
import br.pucminas.diaw.auth.model.Usuario;
import br.pucminas.diaw.auth.repository.TokenRecuperacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {

    private static final int VALIDADE_MINUTOS = 30;

    private final TokenRecuperacaoRepository tokens;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public RecuperacaoSenhaService(TokenRecuperacaoRepository tokens,
                                   UsuarioService usuarioService,
                                   EmailService emailService) {
        this.tokens = tokens;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    /**
     * Gera o token e dispara o email. Nao retorna nada de proposito:
     * a resposta ao usuario e sempre a mesma, exista o email ou nao.
     */
    @Transactional
    public void solicitarRecuperacao(String email) {
        Optional<Usuario> encontrado = usuarioService.buscarPorEmail(email);
        if (encontrado.isEmpty()) {
            return;
        }

        Usuario usuario = encontrado.get();
        tokens.invalidarTokensDoUsuario(usuario);

        TokenRecuperacao token = new TokenRecuperacao(
                UUID.randomUUID().toString(),
                usuario,
                LocalDateTime.now().plusMinutes(VALIDADE_MINUTOS)
        );
        tokens.save(token);

        String link = baseUrl + "/resetpassword?token=" + token.getToken();
        String corpo = """
                Ola, %s!

                Recebemos um pedido para redefinir a sua senha no Horizonte.
                Use o link abaixo para criar uma nova senha:

                %s

                O link vale por %d minutos e so pode ser usado uma vez.
                Se nao foi voce quem pediu, e so ignorar esta mensagem.

                -- Equipe Horizonte
                """.formatted(usuario.getNome(), link, VALIDADE_MINUTOS);

        emailService.enviar(usuario.getEmail(), "Redefinicao de senha - Horizonte", corpo);
    }

    public Optional<TokenRecuperacao> buscarTokenValido(String token) {
        return tokens.findByToken(token == null ? "" : token.trim())
                .filter(TokenRecuperacao::isValido);
    }

    @Transactional
    public boolean redefinirSenha(String token, String novaSenha) {
        Optional<TokenRecuperacao> encontrado = buscarTokenValido(token);
        if (encontrado.isEmpty()) {
            return false;
        }

        TokenRecuperacao registro = encontrado.get();
        usuarioService.trocarSenha(registro.getUsuario(), novaSenha);
        registro.setUsado(true);
        tokens.save(registro);
        return true;
    }
}
