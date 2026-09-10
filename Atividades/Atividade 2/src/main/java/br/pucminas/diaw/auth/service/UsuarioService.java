package br.pucminas.diaw.auth.service;

import br.pucminas.diaw.auth.dto.CadastroForm;
import br.pucminas.diaw.auth.model.Usuario;
import br.pucminas.diaw.auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    public boolean usernameEmUso(String username) {
        return username != null && usuarios.existsByUsernameIgnoreCase(username.trim());
    }

    public boolean emailEmUso(String email) {
        return email != null && usuarios.existsByEmailIgnoreCase(email.trim());
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.findByEmailIgnoreCase(email == null ? "" : email.trim());
    }

    @Transactional
    public Usuario cadastrar(CadastroForm form) {
        Usuario usuario = new Usuario(
                form.getUsername().trim(),
                form.getNome().trim(),
                form.getEmail().trim().toLowerCase(),
                encoder.encode(form.getSenha()) // hash BCrypt, nunca texto puro
        );
        return usuarios.save(usuario);
    }

    @Transactional
    public void trocarSenha(Usuario usuario, String novaSenha) {
        usuario.setSenha(encoder.encode(novaSenha));
        usuarios.save(usuario);
    }
}
