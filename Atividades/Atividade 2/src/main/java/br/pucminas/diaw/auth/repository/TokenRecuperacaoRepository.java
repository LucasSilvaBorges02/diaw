package br.pucminas.diaw.auth.repository;

import br.pucminas.diaw.auth.model.TokenRecuperacao;
import br.pucminas.diaw.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByToken(String token);

    @Modifying
    @Transactional
    @Query("update TokenRecuperacao t set t.usado = true where t.usuario = :usuario and t.usado = false")
    void invalidarTokensDoUsuario(Usuario usuario);
}
