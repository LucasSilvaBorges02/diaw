package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusPedido;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("select p from Pedido p join fetch p.cliente join fetch p.automovel order by p.criadoEm desc")
    List<Pedido> listarTodos();

    @Query("""
            select p from Pedido p join fetch p.cliente join fetch p.automovel
            where p.cliente.id = :clienteId order by p.criadoEm desc""")
    List<Pedido> listarDoCliente(@Param("clienteId") Long clienteId);

    List<Pedido> findByClienteId(Long clienteId);

    boolean existsByAutomovelId(Long automovelId);

    /** Existe outro pedido ativo para o mesmo carro com datas que se cruzam? */
    @Query("""
            select count(p) > 0 from Pedido p
            where p.automovel.id = :automovelId
              and p.id <> :ignorarId
              and p.status in :ativos
              and p.dataInicio < :fim and p.dataFim > :inicio""")
    boolean existeConflito(@Param("automovelId") Long automovelId, @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim, @Param("ignorarId") Long ignorarId,
            @Param("ativos") Collection<StatusPedido> ativos);
}
