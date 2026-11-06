package br.pucminas.aluguel.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProprietario tipoProprietario;

    /** Cliente, empresa ou banco em cujo nome o automóvel fica registrado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito credito;

    @Column(nullable = false)
    private LocalDateTime assinadoEm = LocalDateTime.now();

    protected Contrato() {
    }

    public Contrato(Pedido pedido, Agente agente, TipoProprietario tipoProprietario, Usuario proprietario,
            ContratoCredito credito) {
        this.pedido = pedido;
        this.agente = agente;
        this.tipoProprietario = tipoProprietario;
        this.proprietario = proprietario;
        this.credito = credito;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public TipoProprietario getTipoProprietario() { return tipoProprietario; }
    public Usuario getProprietario() { return proprietario; }
    public Agente getAgente() { return agente; }
    public ContratoCredito getCredito() { return credito; }
    public LocalDateTime getAssinadoEm() { return assinadoEm; }
}
