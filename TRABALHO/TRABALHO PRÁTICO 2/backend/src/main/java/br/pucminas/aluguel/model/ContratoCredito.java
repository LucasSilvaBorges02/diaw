package br.pucminas.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Crédito concedido por um dos bancos agentes para o aluguel. */
@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "banco_id")
    private Agente banco;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    protected ContratoCredito() {
    }

    public ContratoCredito(Agente banco, BigDecimal valor) {
        if (!banco.isBanco()) {
            throw new IllegalArgumentException("Só bancos podem conceder crédito");
        }
        this.banco = banco;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public Agente getBanco() { return banco; }
    public BigDecimal getValor() { return valor; }
}
