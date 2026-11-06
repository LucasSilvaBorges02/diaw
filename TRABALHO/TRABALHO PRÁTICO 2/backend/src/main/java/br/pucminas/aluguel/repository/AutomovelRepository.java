package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Automovel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    List<Automovel> findAllByOrderByMarcaAscModeloAsc();

    boolean existsByPlaca(String placa);

    boolean existsByMatricula(String matricula);
}
