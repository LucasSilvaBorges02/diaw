package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.LoginRequest;
import br.pucminas.aluguel.dto.LoginResponse;
import br.pucminas.aluguel.dto.UsuarioResponse;
import br.pucminas.aluguel.exception.CredenciaisInvalidasException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.repository.UsuarioRepository;
import br.pucminas.aluguel.security.TokenService;
import br.pucminas.aluguel.security.UsuarioLogado;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, TokenService tokens) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest dados) {
        var usuario = usuarios.findByLogin(dados.login().trim())
                .filter(u -> encoder.matches(dados.senha(), u.getSenha()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return new LoginResponse(tokens.gerar(usuario), UsuarioResponse.de(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse eu(UsuarioLogado logado) {
        return usuarios.findById(logado.id()).map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não existe mais"));
    }
}
