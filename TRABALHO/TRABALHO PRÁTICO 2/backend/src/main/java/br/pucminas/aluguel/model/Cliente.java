package br.pucminas.aluguel.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    public static final int MAX_RENDIMENTOS = 3;

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String endereco;

    private String profissao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rendimentos", joinColumns = @JoinColumn(name = "cliente_id"))
    private List<Rendimento> rendimentos = new ArrayList<>();

    @Override
    public Papel getPapel() {
        return Papel.CLIENTE;
    }

    public BigDecimal getRendaTotal() {
        return rendimentos.stream().map(Rendimento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void substituirRendimentos(List<Rendimento> novos) {
        if (novos.size() > MAX_RENDIMENTOS) {
            throw new IllegalArgumentException("O cliente pode ter no máximo " + MAX_RENDIMENTOS + " rendimentos");
        }
        rendimentos.clear();
        rendimentos.addAll(novos);
    }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }
    public List<Rendimento> getRendimentos() { return rendimentos; }
}
