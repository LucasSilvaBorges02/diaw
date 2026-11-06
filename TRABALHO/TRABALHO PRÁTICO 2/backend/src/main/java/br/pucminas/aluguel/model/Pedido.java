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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(length = 500)
    private String parecer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_avaliador_id")
    private Agente agenteAvaliador;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Contrato contrato;

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        this.cliente = cliente;
        alterar(automovel, dataInicio, dataFim);
    }

    public void alterar(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        if (!dataFim.isAfter(dataInicio)) {
            throw new IllegalArgumentException("A data de devolução precisa ser depois da retirada");
        }
        this.automovel = automovel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public long getDias() {
        return ChronoUnit.DAYS.between(dataInicio, dataFim);
    }

    public BigDecimal getValorTotal() {
        return automovel.getValorDiaria().multiply(BigDecimal.valueOf(getDias()));
    }

    public void cancelar() {
        if (!status.podeSerCancelado()) {
            throw new IllegalStateException("Um pedido " + status.name().toLowerCase() + " não pode ser cancelado");
        }
        status = StatusPedido.CANCELADO;
    }

    public void avaliar(Agente agente, boolean aprovado, String parecer) {
        if (status != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Só pedidos pendentes podem ser avaliados");
        }
        this.agenteAvaliador = agente;
        this.parecer = parecer;
        this.status = aprovado ? StatusPedido.APROVADO : StatusPedido.REPROVADO;
    }

    public void registrarContrato(Contrato contrato) {
        if (status != StatusPedido.APROVADO) {
            throw new IllegalStateException("Só pedidos aprovados podem virar contrato");
        }
        this.contrato = contrato;
        this.status = StatusPedido.CONTRATADO;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Automovel getAutomovel() { return automovel; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusPedido getStatus() { return status; }
    public String getParecer() { return parecer; }
    public Agente getAgenteAvaliador() { return agenteAvaliador; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public Contrato getContrato() { return contrato; }
}
