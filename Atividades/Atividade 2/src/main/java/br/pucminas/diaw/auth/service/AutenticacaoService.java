package br.pucminas.diaw.auth.service;

import br.pucminas.diaw.auth.model.Usuario;
import br.pucminas.diaw.auth.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Carrega o usuario para o Spring Security.
 * Aceita login por nome de usuario OU por email.
 */
@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarios;

    public AutenticacaoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        String valor = identificador == null ? "" : identificador.trim();

        Usuario usuario = usuarios
                .findByUsernameIgnoreCaseOrEmailIgnoreCase(valor, valor)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getPapel())))
                .disabled(!usuario.isAtivo())
                .build();
    }
}
