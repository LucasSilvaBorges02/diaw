package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.TipoAgente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteRepository extends JpaRepository<Agente, Long> {

    List<Agente> findByTipoOrderByNomeAsc(TipoAgente tipo);
}
